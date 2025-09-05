package click.reelscout.backend.service.implementation;

import click.reelscout.backend.builder.definition.UserBuilder;
import click.reelscout.backend.dto.request.UserPasswordChangeRequestDTO;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.dto.response.UserLoginResponseDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.exception.custom.EntityNotFoundException;
import click.reelscout.backend.exception.custom.EntityUpdateException;
import click.reelscout.backend.factory.UserMapperFactoryRegistry;
import click.reelscout.backend.mapper.definition.UserMapper;
import click.reelscout.backend.model.jpa.Role;
import click.reelscout.backend.model.jpa.User;
import click.reelscout.backend.repository.elasticsearch.UserElasticRepository;
import click.reelscout.backend.repository.jpa.UserRepository;
import click.reelscout.backend.s3.S3Service;
import click.reelscout.backend.service.definition.AuthService;
import click.reelscout.backend.strategy.UserMapperContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.RETURNS_SELF;

/**
 * Pure unit tests for {@link UserServiceImplementation}.
 * <p>
 * - No Spring context is started.
 * - All collaborators are mocked.
 * - We call service methods directly and verify interactions and returned values.
 * <p>
 * Type parameters are erased by using raw types for readability in tests.
 * This does not change the behavior under test.
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"rawtypes", "unchecked"})
class UserServiceImplementationTest {

    @Mock private UserRepository userRepository;
    @Mock private UserElasticRepository userElasticRepository;
    @Mock private UserMapperContext userMapperContext;
    @Mock private UserMapperFactoryRegistry registry;
    @Mock private S3Service s3Service;
    @Mock private AuthService authService;
    @Mock private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImplementation service;

    /** Concrete request DTO used just to satisfy the R type parameter in a few tests. */
    static class TestUserRequestDTO extends UserRequestDTO { }

    private User mockUser(Long id, String username, String email, String s3Key, String encodedPwd) {
        User u = mock(User.class);
        lenient().when(u.getId()).thenReturn(id);
        lenient().when(u.getUsername()).thenReturn(username);
        lenient().when(u.getEmail()).thenReturn(email);
        lenient().when(u.getS3ImageKey()).thenReturn(s3Key);
        lenient().when(u.getPassword()).thenReturn(encodedPwd);
        lenient().when(u.getRole()).thenReturn(Role.MEMBER);
        return u;
    }

    @BeforeEach
    void wireRegistry() {
        lenient().when(registry.getMapperFor(any(User.class))).thenReturn(mock(UserMapper.class));
        lenient().when(registry.getMapperFor(any(UserRequestDTO.class))).thenReturn(mock(UserMapper.class));
    }

    @Test
    @DisplayName("getAll: maps every user to DTO with its S3 image")
    void getAll_mapsEveryUser() {
        User u1 = mockUser(1L, "u1", "e1@mail.com", "k1", "enc");
        User u2 = mockUser(2L, "u2", "e2@mail.com", "k2", "enc");
        when(userRepository.findAll()).thenReturn(List.of(u1, u2));
        when(s3Service.getFile("k1")).thenReturn("img1");
        when(s3Service.getFile("k2")).thenReturn("img2");

        UserResponseDTO d1 = new UserResponseDTO();
        UserResponseDTO d2 = new UserResponseDTO();
        when(userMapperContext.toDto(u1, "img1")).thenReturn(d1);
        when(userMapperContext.toDto(u2, "img2")).thenReturn(d2);

        List<UserResponseDTO> out = service.getAll();

        assertEquals(2, out.size());
        assertSame(d1, out.get(0));
        assertSame(d2, out.get(1));
        verify(userRepository).findAll();
        verify(userMapperContext, times(2)).setUserMapper(any());
        verify(s3Service).getFile("k1");
        verify(s3Service).getFile("k2");
    }

    @Test
    @DisplayName("getById: finds, maps to DTO, includes S3 image")
    void getById_maps() {
        User u = mockUser(10L, "u", "e@mail", "key", "enc");
        when(userRepository.findById(10L)).thenReturn(Optional.of(u));
        when(s3Service.getFile("key")).thenReturn("img");
        UserResponseDTO dto = new UserResponseDTO();
        when(userMapperContext.toDto(u, "img")).thenReturn(dto);

        UserResponseDTO out = service.getById(10L);

        assertSame(dto, out);
    }

    @Test
    @DisplayName("getById: throws EntityNotFoundException when not found")
    void getById_notFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.getById(99L));
    }

    @Test
    @DisplayName("getByEmail: happy path")
    void getByEmail_maps() {
        User u = mockUser(1L, "u", "e@mail", "k", "enc");
        when(userRepository.findByEmail("e@mail")).thenReturn(Optional.of(u));
        when(s3Service.getFile("k")).thenReturn("img");
        UserResponseDTO dto = new UserResponseDTO();
        when(userMapperContext.toDto(u, "img")).thenReturn(dto);

        UserResponseDTO out = service.getByEmail("e@mail");
        assertSame(dto, out);
    }

    @Test
    @DisplayName("getByUsername: happy path")
    void getByUsername_maps() {
        User u = mockUser(1L, "john", "e@mail", "k", "enc");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(u));
        when(s3Service.getFile("k")).thenReturn("img");
        UserResponseDTO dto = new UserResponseDTO();
        when(userMapperContext.toDto(u, "img")).thenReturn(dto);

        UserResponseDTO out = service.getByUsername("john");
        assertSame(dto, out);
    }

    @Test
    @DisplayName("getByUsernameOrEmail: happy path")
    void getByUsernameOrEmail_maps() {
        User u = mockUser(1L, "john", "john@mail", "k", "enc");
        when(userRepository.findByUsernameOrEmail("john")).thenReturn(Optional.of(u));
        when(s3Service.getFile("k")).thenReturn("img");
        UserResponseDTO dto = new UserResponseDTO();
        when(userMapperContext.toDto(u, "img")).thenReturn(dto);

        UserResponseDTO out = service.getByUsernameOrEmail("john");
        assertSame(dto, out);
    }

    @Test
    @DisplayName("getCurrentUserDto: maps current principal with its image")
    void getCurrentUserDto_maps() {
        User principal = mockUser(7L, "me", "me@mail", "k7", "enc");
        when(s3Service.getFile("k7")).thenReturn("img7");
        UserResponseDTO dto = new UserResponseDTO();
        when(userMapperContext.toDto(principal, "img7")).thenReturn(dto);

        UserResponseDTO out = service.getCurrentUserDto(principal);
        assertSame(dto, out);
    }

    @Test
    @DisplayName("update: wrong current password -> EntityUpdateException")
    void update_wrongCurrentPassword_throws() {
        User auth = mockUser(1L, "u", "e", null, "ENC");
        TestUserRequestDTO req = new TestUserRequestDTO();
        req.setPassword("raw");
        when(passwordEncoder.matches("raw", "ENC")).thenReturn(false);

        assertThrows(EntityUpdateException.class, () -> service.update(auth, req));
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("update: email conflict -> EntityUpdateException")
    void update_emailConflict_throws() {
        User auth = mockUser(1L, "u", "e@mail", null, "ENC");
        TestUserRequestDTO req = new TestUserRequestDTO();
        req.setPassword("raw");
        req.setEmail("new@mail");
        when(passwordEncoder.matches("raw", "ENC")).thenReturn(true);
        when(userRepository.existsByEmailAndIdIsNot("new@mail", 1L)).thenReturn(true);

        assertThrows(EntityUpdateException.class, () -> service.update(auth, req));
    }

    @Test
    @DisplayName("update: username conflict -> EntityUpdateException")
    void update_usernameConflict_throws() {
        User auth = mockUser(1L, "u", "e@mail", null, "ENC");
        TestUserRequestDTO req = new TestUserRequestDTO();
        req.setPassword("raw");
        req.setEmail("ok@mail");
        req.setUsername("newU");
        when(passwordEncoder.matches("raw", "ENC")).thenReturn(true);
        when(userRepository.existsByEmailAndIdIsNot("ok@mail", 1L)).thenReturn(false);
        when(userRepository.existsByUsernameAndIdIsNot("newU", 1L)).thenReturn(true);

        assertThrows(EntityUpdateException.class, () -> service.update(auth, req));
    }

    @Test
    @DisplayName("update: success, changes present (superEquals false) -> returns login token")
    void update_success_changesPresent_returnsLoginToken() {
        User auth = mockUser(1L, "oldU", "old@mail", "s3Key", "ENC");
        TestUserRequestDTO req = new TestUserRequestDTO();
        req.setPassword("raw");
        req.setEmail("ok@mail");
        req.setUsername("newU");
        req.setBase64Image("base64");

        when(passwordEncoder.matches("raw", "ENC")).thenReturn(true);
        when(userRepository.existsByEmailAndIdIsNot("ok@mail", 1L)).thenReturn(false);
        when(userRepository.existsByUsernameAndIdIsNot("newU", 1L)).thenReturn(false);

        User reqEntity = mockUser(null, "newU", "ok@mail", null, "ENC");
        when(userMapperContext.toEntity(eq(req), any())).thenReturn(reqEntity);

        UserBuilder mockBuilder = mock(UserBuilder.class, RETURNS_SELF);
        when(userMapperContext.toBuilder(reqEntity)).thenReturn(mockBuilder);

        User updated = mockUser(1L, "newU", "ok@mail", "anyS3", "ENC");
        when(mockBuilder.build()).thenReturn(updated);

        when(userRepository.save(updated)).thenReturn(updated);
        lenient().when(userMapperContext.toUserDoc(updated)).thenReturn(null);
        when(authService.login("newU", "raw")).thenReturn(new UserLoginResponseDTO("jwt"));

        UserLoginResponseDTO out = service.update(auth, req);

        assertNotNull(out);
        assertEquals("jwt", out.getAccessToken());
        verify(userRepository).save(updated);
        verify(userElasticRepository).save(any());
        verify(s3Service).uploadFile(anyString(), eq("base64"));
    }

    @Test
    @DisplayName("update: success, no changes (superEquals true) -> returns null")
    void update_success_noChanges_returnsNull() {
        User auth = mockUser(1L, "u", "e@mail", "s3", "ENC");
        TestUserRequestDTO req = new TestUserRequestDTO();
        req.setPassword("raw");
        req.setEmail("e@mail");
        req.setUsername("u");

        when(passwordEncoder.matches("raw", "ENC")).thenReturn(true);
        when(userRepository.existsByEmailAndIdIsNot("e@mail", 1L)).thenReturn(false);
        when(userRepository.existsByUsernameAndIdIsNot("u", 1L)).thenReturn(false);

        User reqEntity = mockUser(null, "u", "e@mail", "s3", "ENC");
        when(userMapperContext.toEntity(eq(req), any())).thenReturn(reqEntity);

        UserBuilder mockBuilder = mock(UserBuilder.class, RETURNS_SELF);
        when(userMapperContext.toBuilder(reqEntity)).thenReturn(mockBuilder);

        User updated = mockUser(1L, "u", "e@mail", "s3", "ENC");
        when(mockBuilder.build()).thenReturn(updated);

        when(userRepository.save(updated)).thenReturn(updated);
        when(auth.superEquals(updated)).thenReturn(true);

        UserLoginResponseDTO out = service.update(auth, req);

        assertNull(out);
        verify(userRepository).save(updated);
        verifyNoInteractions(authService);
    }

    @Test
    @DisplayName("update: any repository/S3 error -> wraps into EntityUpdateException")
    void update_wrapsIntoEntityUpdateException() {
        User auth = mockUser(1L, "u", "e@mail", null, "ENC");
        TestUserRequestDTO req = new TestUserRequestDTO();
        req.setPassword("raw");
        req.setEmail("e@mail");
        req.setUsername("u");

        when(passwordEncoder.matches("raw", "ENC")).thenReturn(true);
        when(userRepository.existsByEmailAndIdIsNot("e@mail", 1L)).thenReturn(false);
        when(userRepository.existsByUsernameAndIdIsNot("u", 1L)).thenReturn(false);

        User reqEntity = mockUser(null, "u", "e@mail", null, "ENC");
        when(userMapperContext.toEntity(eq(req), any())).thenReturn(reqEntity);

        UserBuilder mockBuilder = mock(UserBuilder.class, RETURNS_SELF);
        when(userMapperContext.toBuilder(reqEntity)).thenReturn(mockBuilder);

        User updated = mockUser(1L, "u", "e@mail", null, "ENC");
        when(mockBuilder.build()).thenReturn(updated);

        when(userRepository.save(updated)).thenThrow(new RuntimeException("boom"));

        assertThrows(EntityUpdateException.class, () -> service.update(auth, req));
    }

    @Test
    @DisplayName("changePassword: wrong current password -> EntityUpdateException")
    void changePassword_wrongCurrent_throws() {
        User auth = mockUser(1L, "u", "e", null, "ENC");
        UserPasswordChangeRequestDTO req = new UserPasswordChangeRequestDTO();
        req.setCurrentPassword("raw");
        when(passwordEncoder.matches("raw", "ENC")).thenReturn(false);

        assertThrows(EntityUpdateException.class, () -> service.changePassword(auth, req));
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("changePassword: new/confirm mismatch -> EntityUpdateException")
    void changePassword_mismatch_throws() {
        User auth = mockUser(1L, "u", "e", null, "ENC");
        UserPasswordChangeRequestDTO req = new UserPasswordChangeRequestDTO();
        req.setCurrentPassword("raw");
        req.setNewPassword("n1");
        req.setConfirmPassword("n2");
        when(passwordEncoder.matches("raw", "ENC")).thenReturn(true);

        assertThrows(EntityUpdateException.class, () -> service.changePassword(auth, req));
    }

    @Test
    @DisplayName("changePassword: success -> encodes, saves and returns OK message")
    void changePassword_success() {
        User auth = mockUser(1L, "u", "e", null, "ENC");
        UserPasswordChangeRequestDTO req = new UserPasswordChangeRequestDTO();
        req.setCurrentPassword("raw");
        req.setNewPassword("newP");
        req.setConfirmPassword("newP");

        when(passwordEncoder.matches("raw", "ENC")).thenReturn(true);
        when(passwordEncoder.encode("newP")).thenReturn("ENC2");

        UserBuilder mockBuilder = mock(UserBuilder.class, RETURNS_SELF);
        when(userMapperContext.toBuilder(auth)).thenReturn(mockBuilder);

        User updated = mockUser(1L, "u", "e", null, "ENC2");
        when(mockBuilder.build()).thenReturn(updated);

        CustomResponseDTO out = service.changePassword(auth, req);

        assertEquals("Password changed successfully", out.getMessage());
        verify(userRepository).save(updated);
    }

    @Test
    @DisplayName("changePassword: repository throws -> wraps into EntityUpdateException")
    void changePassword_repoError_wraps() {
        User auth = mockUser(1L, "u", "e", null, "ENC");
        UserPasswordChangeRequestDTO req = new UserPasswordChangeRequestDTO();
        req.setCurrentPassword("raw");
        req.setNewPassword("newP");
        req.setConfirmPassword("newP");

        when(passwordEncoder.matches("raw", "ENC")).thenReturn(true);
        when(passwordEncoder.encode("newP")).thenReturn("ENC2");

        UserBuilder mockBuilder = mock(UserBuilder.class, RETURNS_SELF);
        when(userMapperContext.toBuilder(auth)).thenReturn(mockBuilder);

        User updated = mockUser(1L, "u", "e", null, "ENC2");
        when(mockBuilder.build()).thenReturn(updated);

        doThrow(new RuntimeException("db down")).when(userRepository).save(updated);

        assertThrows(EntityUpdateException.class, () -> service.changePassword(auth, req));
    }
}

