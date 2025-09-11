package click.reelscout.backend.service.implementation;

import click.reelscout.backend.dto.request.UserLoginRequestDTO;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.UserLoginResponseDTO;
import click.reelscout.backend.exception.custom.EntityCreateException;
import click.reelscout.backend.exception.custom.EntityNotFoundException;
import click.reelscout.backend.exception.custom.InvalidCredentialsException;
import click.reelscout.backend.factory.UserMapperFactoryRegistry;
import click.reelscout.backend.mapper.definition.UserMapper;
import click.reelscout.backend.model.elasticsearch.UserDoc;
import click.reelscout.backend.model.jpa.User;
import click.reelscout.backend.repository.elasticsearch.UserElasticRepository;
import click.reelscout.backend.repository.jpa.UserRepository;
import click.reelscout.backend.s3.S3Service;
import click.reelscout.backend.security.JwtService;
import click.reelscout.backend.strategy.UserMapperContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for {@link AuthServiceImplementation}.
 * This class contains unit tests for the authentication service implementation,
 * focusing on functionality related to user login and registration processes.
 * <p>
 * Test cases utilize the Mockito framework for mocking dependencies and verifying interactions.
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"rawtypes", "unchecked"})
class AuthServiceImplementationTest {

    @Mock private UserRepository<User> userRepository;
    @Mock private UserElasticRepository userElasticRepository;
    @Mock private JwtService jwtService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserMapperContext userMapperContext;
    @Mock private UserMapperFactoryRegistry userMapperFactoryRegistry;
    @Mock private UserMapper userMapper;
    @Mock private S3Service s3Service;

    @InjectMocks
    private AuthServiceImplementation service;

    private final String username = "john";
    private final String email = "john@mail.com";
    private final String rawPwd = "pwd";
    private final String encPwd = "enc";

    @BeforeEach
    void setupCommon() {
        // nothing special; mocks are injected by @InjectMocks
    }

    /**
     * Tests the login functionality of the authentication service.
     * Verifies that a valid JWT token is returned upon successful authentication.
     * Also checks that appropriate exceptions are thrown for invalid credentials or missing users.
     */
    @Test
    @DisplayName("login(username,password): returns JWT on successful authentication")
    void login_success_returnsToken() {
        User user = mock(User.class);
        when(userRepository.findByUsernameOrEmail(username)).thenReturn(Optional.of(user));
        when(user.getPassword()).thenReturn(encPwd);
        when(passwordEncoder.matches(rawPwd, encPwd)).thenReturn(true);
        when(userMapperFactoryRegistry.getMapperFor(user)).thenReturn(userMapper);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        UserLoginResponseDTO res = service.login(username, rawPwd);

        assertNotNull(res);
        assertEquals("jwt-token", res.getAccessToken());
        verify(userMapperContext).setUserMapper(userMapper);
        verify(jwtService).generateToken(user);
    }

    /**
     * Tests the login functionality when the user is not found in the repository.
     * Verifies that an EntityNotFoundException is thrown in such cases.
     * Also ensures that no JWT token generation is attempted.
     */
    @Test
    @DisplayName("login(username,password): throws EntityNotFoundException when user is missing")
    void login_userNotFound_throws() {
        when(userRepository.findByUsernameOrEmail(username)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.login(username, rawPwd));
        verifyNoInteractions(jwtService);
    }

    /**
     * Tests the login functionality when the provided password does not match the stored password.
     * Verifies that an InvalidCredentialsException is thrown in such cases.
     * Also ensures that no JWT token generation is attempted.
     */
    @Test
    @DisplayName("login(username,password): throws InvalidCredentialsException when password does not match")
    void login_badPassword_throws() {
        User user = mock(User.class);
        when(userRepository.findByUsernameOrEmail(username)).thenReturn(Optional.of(user));
        when(user.getPassword()).thenReturn(encPwd);
        when(passwordEncoder.matches(rawPwd, encPwd)).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> service.login(username, rawPwd));
        verify(jwtService, never()).generateToken(any());
    }

    /**
     * Tests the login functionality using a UserLoginRequestDTO.
     * Verifies that the method correctly delegates to the login(username, password) method.
     * Also checks that a valid JWT token is returned upon successful authentication.
     */
    @Test
    @DisplayName("login(UserLoginRequestDTO): delegates to login(username,password)")
    void login_withDto_delegates() {
        UserLoginRequestDTO dto = new UserLoginRequestDTO();
        dto.setUsername(username);
        dto.setPassword(rawPwd);

        User user = mock(User.class);
        when(userRepository.findByUsernameOrEmail(username)).thenReturn(Optional.of(user));
        when(user.getPassword()).thenReturn(encPwd);
        when(passwordEncoder.matches(rawPwd, encPwd)).thenReturn(true);
        when(userMapperFactoryRegistry.getMapperFor(user)).thenReturn(userMapper);
        when(jwtService.generateToken(user)).thenReturn("jwt");

        UserLoginResponseDTO res = service.login(dto);

        assertEquals("jwt", res.getAccessToken());
    }

    // ---------- REGISTER ----------

    /**
     * Tests the register functionality when the username or email already exists in the repository.
     * Verifies that an EntityCreateException is thrown in such cases.
     * Also ensures that no user persistence, Elasticsearch indexing, or S3 upload is attempted.
     */
    @Test
    @DisplayName("register(): throws EntityCreateException when username or email already exists")
    void register_existingUser_throws() {
        UserRequestDTO req = mock(UserRequestDTO.class);
        when(req.getUsername()).thenReturn(username);
        when(req.getEmail()).thenReturn(email);
        when(userRepository.existsByUsernameOrEmail(username, email)).thenReturn(true);

        assertThrows(EntityCreateException.class, () -> service.register(req));

        verify(userRepository, never()).save(any());
        verify(userElasticRepository, never()).save(any());
        verify(s3Service, never()).uploadFile(any(), any());
    }

    /**
     * Tests the successful registration flow with a base64 image provided.
     * Verifies that the service generates an S3 key, persists the user, indexes in Elasticsearch,
     * uploads the image to S3, and returns a JWT token.
     * Also checks that the same S3 key is used for both entity creation and image upload.
     */
    @Test
    @DisplayName("register(): success flow with base64 image -> generates S3 key, persists user, indexes in ES, uploads image, and returns JWT")
    void register_success_withImage() {
        // Request with base64 image
        UserRequestDTO req = mock(UserRequestDTO.class);
        when(req.getUsername()).thenReturn(username);
        when(req.getEmail()).thenReturn(email);
        when(req.getPassword()).thenReturn(rawPwd);
        when(req.getBase64Image()).thenReturn("base64-image");

        when(userRepository.existsByUsernameOrEmail(username, email)).thenReturn(false);
        when(userMapperFactoryRegistry.getMapperFor(req)).thenReturn(userMapper);

        // Entity build & save
        User entityToSave = mock(User.class);
        User saved = mock(User.class);
        when(userRepository.save(entityToSave)).thenReturn(saved);

        // The service will compute a UUID-based key; capture it where it's passed
        ArgumentCaptor<String> s3KeyCaptorToEntity = ArgumentCaptor.forClass(String.class);
        when(userMapperContext.toEntity(eq(req), s3KeyCaptorToEntity.capture())).thenReturn(entityToSave);

        // ES indexing
        UserDoc doc = mock(UserDoc.class);
        when(userMapperContext.toUserDoc(saved)).thenReturn(doc);

        // After register(), service calls login(username,password)
        when(userRepository.findByUsernameOrEmail(username)).thenReturn(Optional.of(saved));
        when(saved.getPassword()).thenReturn(encPwd);
        when(passwordEncoder.matches(rawPwd, encPwd)).thenReturn(true);
        when(userMapperFactoryRegistry.getMapperFor(saved)).thenReturn(userMapper);
        when(jwtService.generateToken(saved)).thenReturn("jwt-registered");

        UserLoginResponseDTO res = service.register(req);

        assertEquals("jwt-registered", res.getAccessToken());

        // Verify ES save and S3 upload with same generated key
        ArgumentCaptor<String> s3KeyCaptorToUpload = ArgumentCaptor.forClass(String.class);
        verify(s3Service).uploadFile(s3KeyCaptorToUpload.capture(), eq("base64-image"));

        String keyToEntity = s3KeyCaptorToEntity.getValue();
        String keyToUpload = s3KeyCaptorToUpload.getValue();
        assertNotNull(keyToEntity);
        assertTrue(keyToEntity.startsWith("user/"), "S3 key must start with 'user/'");
        assertEquals(keyToEntity, keyToUpload, "Key passed to entity builder and S3 upload must be the same");

        verify(userElasticRepository).save(doc);
        verify(userMapperContext, times(2)).setUserMapper(userMapper); // once for DTO path, once during login
    }

    /**
     * Tests the successful registration flow without a base64 image provided.
     * Verifies that the service does not generate an S3 key, persists the user, indexes in Elasticsearch,
     * attempts to upload a null image to S3, and returns a JWT token.
     * Also checks that the S3 upload is called with null parameters.
     */
    @Test
    @DisplayName("register(): success flow without image -> S3 key is null and upload called with nulls")
    void register_success_withoutImage() {
        UserRequestDTO req = mock(UserRequestDTO.class);
        when(req.getUsername()).thenReturn(username);
        when(req.getEmail()).thenReturn(email);
        when(req.getPassword()).thenReturn(rawPwd);
        when(req.getBase64Image()).thenReturn(null);

        when(userRepository.existsByUsernameOrEmail(username, email)).thenReturn(false);
        when(userMapperFactoryRegistry.getMapperFor(req)).thenReturn(userMapper);

        User entityToSave = mock(User.class);
        User saved = mock(User.class);
        when(userRepository.save(entityToSave)).thenReturn(saved);

        // Expect null S3 key when no image is present
        when(userMapperContext.toEntity(eq(req), isNull())).thenReturn(entityToSave);

        UserDoc doc = mock(UserDoc.class);
        when(userMapperContext.toUserDoc(saved)).thenReturn(doc);

        // Login after register
        when(userRepository.findByUsernameOrEmail(username)).thenReturn(Optional.of(saved));
        when(saved.getPassword()).thenReturn(encPwd);
        when(passwordEncoder.matches(rawPwd, encPwd)).thenReturn(true);
        when(userMapperFactoryRegistry.getMapperFor(saved)).thenReturn(userMapper);
        when(jwtService.generateToken(saved)).thenReturn("jwt-registered");

        UserLoginResponseDTO res = service.register(req);

        assertEquals("jwt-registered", res.getAccessToken());
        verify(s3Service).uploadFile(null, null);
        verify(userElasticRepository).save(doc);
    }

    /**
     * Tests the behavior of the `register` method when an unexpected exception occurs during
     * the persistence, indexing, or file upload process.
     * <p>
     * This test ensures that any exception thrown during these operations is wrapped
     * in an `EntityCreateException` and that no partial operations, such as saving
     * the entity in the repository, indexing it in Elasticsearch, or generating a JWT token,
     * are executed.
     * <p>
     * Scenarios validated by this test:
     * - An exception during the `toEntity` mapping process raises an `EntityCreateException`.
     * - Verifies that the user repository, Elasticsearch repository, and JWT service
     *   methods are never called when an exception occurs.
     */
    @Test
    @DisplayName("register(): wraps any exception during persistence/index/upload into EntityCreateException")
    void register_internalError_wrappedAsEntityCreateException() {
        UserRequestDTO req = mock(UserRequestDTO.class);
        when(req.getUsername()).thenReturn(username);
        when(req.getEmail()).thenReturn(email);
        when(req.getBase64Image()).thenReturn("img");

        when(userRepository.existsByUsernameOrEmail(username, email)).thenReturn(false);
        when(userMapperFactoryRegistry.getMapperFor(req)).thenReturn(userMapper);

        // Throw inside the try-block (e.g., repository failure)
        when(userMapperContext.toEntity(any(), any())).thenThrow(new RuntimeException("db down"));

        assertThrows(EntityCreateException.class, () -> service.register(req));

        verify(userRepository, never()).save(any());
        verify(userElasticRepository, never()).save(any());
        verify(jwtService, never()).generateToken(any());
    }
}