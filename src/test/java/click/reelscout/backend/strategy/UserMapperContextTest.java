package click.reelscout.backend.strategy;

import click.reelscout.backend.builder.definition.UserBuilder;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.mapper.definition.UserMapper;
import click.reelscout.backend.model.elasticsearch.UserDoc;
import click.reelscout.backend.model.jpa.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the UserMapperContext class.
 * This test class verifies the behavior of UserMapperContext methods,
 * ensuring they correctly delegate to the UserMapper and handle various scenarios.
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"rawtypes", "unchecked"})
class UserMapperContextTest {

    @Mock
    private UserMapper mockUserMapper;

    @Mock
    private User mockUser;

    @Mock
    private UserRequestDTO mockUserRequestDTO;

    @Mock
    private UserResponseDTO mockUserResponseDTO;

    @Mock
    private UserBuilder mockUserBuilder;

    @Mock
    private UserDoc mockUserDoc;

    private UserMapperContext context;

    @BeforeEach
    void setUp() {
        context = new UserMapperContext<>();
    }

    /**
     * Tests that the setUserMapper method correctly sets the mapper and verifies its usage.
     */
    @Test
    @DisplayName("setUserMapper: should set the mapper correctly")
    void setUserMapper_shouldSetMapperCorrectly() {
        // Act
        context.setUserMapper(mockUserMapper);

        // Assert - we can verify this by calling a method that uses the mapper
        when(mockUserMapper.toDto(mockUser, "test-key")).thenReturn(mockUserResponseDTO);

        UserResponseDTO result = context.toDto(mockUser, "test-key");

        assertSame(mockUserResponseDTO, result);
        verify(mockUserMapper).toDto(mockUser, "test-key");
    }

    /**
     * Tests that the toDto method delegates to the mapper with the correct parameters.
     */
    @Test
    @DisplayName("toDto: should delegate to mapper with correct parameters")
    void toDto_shouldDelegateToMapper() {
        // Arrange
        context.setUserMapper(mockUserMapper);
        String s3ImageKey = "test-s3-key";
        when(mockUserMapper.toDto(mockUser, s3ImageKey)).thenReturn(mockUserResponseDTO);

        // Act
        UserResponseDTO result = context.toDto(mockUser, s3ImageKey);

        // Assert
        assertSame(mockUserResponseDTO, result);
        verify(mockUserMapper).toDto(mockUser, s3ImageKey);
    }

    /**
     * Tests that the toDto method throws a NullPointerException when the mapper is null.
     */
    @Test
    @DisplayName("toDto: should throw NullPointerException when mapper is null")
    void toDto_shouldThrowWhenMapperIsNull() {
        // Arrange - context has no mapper set

        // Act & Assert
        assertThrows(NullPointerException.class, () -> context.toDto(mockUser, "test-key"));
    }

    /**
     * Tests that the toBuilder method delegates to the mapper with the correct parameters.
     */
    @Test
    @DisplayName("toBuilder: should delegate to mapper with correct parameters")
    void toBuilder_shouldDelegateToMapper() {
        // Arrange
        context.setUserMapper(mockUserMapper);
        when(mockUserMapper.toBuilder(mockUser)).thenReturn(mockUserBuilder);

        // Act
        UserBuilder result = context.toBuilder(mockUser);

        // Assert
        assertSame(mockUserBuilder, result);
        verify(mockUserMapper).toBuilder(mockUser);
    }

    /**
     * Tests that the toBuilder method throws a NullPointerException when the mapper is null.
     */
    @Test
    @DisplayName("toBuilder: should throw NullPointerException when mapper is null")
    void toBuilder_shouldThrowWhenMapperIsNull() {
        // Arrange - context has no mapper set

        // Act & Assert
        assertThrows(NullPointerException.class, () -> context.toBuilder(mockUser));
    }

    /**
     * Tests that the toEntity method delegates to the mapper with the correct parameters.
     */
    @Test
    @DisplayName("toEntity: should delegate to mapper with correct parameters")
    void toEntity_shouldDelegateToMapper() {
        // Arrange
        context.setUserMapper(mockUserMapper);
        String s3ImageKey = "test-s3-key";
        when(mockUserMapper.toEntity(mockUserRequestDTO, s3ImageKey)).thenReturn(mockUser);

        // Act
        User result = context.toEntity(mockUserRequestDTO, s3ImageKey);

        // Assert
        assertSame(mockUser, result);
        verify(mockUserMapper).toEntity(mockUserRequestDTO, s3ImageKey);
    }

    /**
     * Tests that the toEntity method throws a NullPointerException when the mapper is null.
     */
    @Test
    @DisplayName("toEntity: should throw NullPointerException when mapper is null")
    void toEntity_shouldThrowWhenMapperIsNull() {
        // Arrange - context has no mapper set

        // Act & Assert
        assertThrows(NullPointerException.class, () -> context.toEntity(mockUserRequestDTO, "test-key"));
    }

    /**
     * Tests that the toUserDoc method delegates to the mapper with the correct parameters.
     */
    @Test
    @DisplayName("toUserDoc: should delegate to mapper with correct parameters")
    void toUserDoc_shouldDelegateToMapper() {
        // Arrange
        context.setUserMapper(mockUserMapper);
        when(mockUserMapper.toDoc(mockUser)).thenReturn(mockUserDoc);

        // Act
        UserDoc result = context.toUserDoc(mockUser);

        // Assert
        assertSame(mockUserDoc, result);
        verify(mockUserMapper).toDoc(mockUser);
    }

    /**
     * Tests that the toUserDoc method throws a NullPointerException when the mapper is null.
     */
    @Test
    @DisplayName("toUserDoc: should throw NullPointerException when mapper is null")
    void toUserDoc_shouldThrowWhenMapperIsNull() {
        // Arrange - context has no mapper set

        // Act & Assert
        assertThrows(NullPointerException.class, () -> context.toUserDoc(mockUser));
    }

    /**
     * Tests that the toDto method handles null user parameter correctly.
     */
    @Test
    @DisplayName("toDto: should handle null user parameter correctly")
    void toDto_shouldHandleNullUser() {
        // Arrange
        context.setUserMapper(mockUserMapper);
        String s3ImageKey = "test-key";
        when(mockUserMapper.toDto(null, s3ImageKey)).thenReturn(null);

        // Act
        UserResponseDTO result = context.toDto(null, s3ImageKey);

        // Assert
        assertNull(result);
        verify(mockUserMapper).toDto(null, s3ImageKey);
    }

    /**
     * Tests that the toDto method handles null s3ImageKey parameter correctly.
     */
    @Test
    @DisplayName("toDto: should handle null s3ImageKey parameter correctly")
    void toDto_shouldHandleNullS3ImageKey() {
        // Arrange
        context.setUserMapper(mockUserMapper);
        when(mockUserMapper.toDto(mockUser, null)).thenReturn(mockUserResponseDTO);

        // Act
        UserResponseDTO result = context.toDto(mockUser, null);

        // Assert
        assertSame(mockUserResponseDTO, result);
        verify(mockUserMapper).toDto(mockUser, null);
    }

    /**
     * Tests that the toEntity method handles null userRequestDTO parameter correctly.
     */
    @Test
    @DisplayName("toEntity: should handle null userRequestDTO parameter correctly")
    void toEntity_shouldHandleNullUserRequestDTO() {
        // Arrange
        context.setUserMapper(mockUserMapper);
        String s3ImageKey = "test-key";
        when(mockUserMapper.toEntity(null, s3ImageKey)).thenReturn(null);

        // Act
        User result = context.toEntity(null, s3ImageKey);

        // Assert
        assertNull(result);
        verify(mockUserMapper).toEntity(null, s3ImageKey);
    }

    /**
     * Tests that the toEntity method handles null s3ImageKey parameter correctly.
     */
    @Test
    @DisplayName("toEntity: should handle null s3ImageKey parameter correctly")
    void toEntity_shouldHandleNullS3ImageKey() {
        // Arrange
        context.setUserMapper(mockUserMapper);
        when(mockUserMapper.toEntity(mockUserRequestDTO, null)).thenReturn(mockUser);

        // Act
        User result = context.toEntity(mockUserRequestDTO, null);

        // Assert
        assertSame(mockUser, result);
        verify(mockUserMapper).toEntity(mockUserRequestDTO, null);
    }

    /**
     * Tests that the toBuilder method handles null user parameter correctly.
     */
    @Test
    @DisplayName("toBuilder: should handle null user parameter correctly")
    void toBuilder_shouldHandleNullUser() {
        // Arrange
        context.setUserMapper(mockUserMapper);
        when(mockUserMapper.toBuilder(null)).thenReturn(null);

        // Act
        UserBuilder result = context.toBuilder(null);

        // Assert
        assertNull(result);
        verify(mockUserMapper).toBuilder(null);
    }

    /**
     * Tests that the toUserDoc method handles null user parameter correctly.
     */
    @Test
    @DisplayName("toUserDoc: should handle null user parameter correctly")
    void toUserDoc_shouldHandleNullUser() {
        // Arrange
        context.setUserMapper(mockUserMapper);
        when(mockUserMapper.toDoc(null)).thenReturn(null);

        // Act
        UserDoc result = context.toUserDoc(null);

        // Assert
        assertNull(result);
        verify(mockUserMapper).toDoc(null);
    }

    /**
     * Tests multiple operations using the same mapper instance to ensure consistent behavior.
     */
    @Test
    @DisplayName("multiple operations: should work correctly with same mapper instance")
    void multipleOperations_shouldWorkWithSameMapper() {
        // Arrange
        context.setUserMapper(mockUserMapper);
        String s3Key = "multi-test-key";

        when(mockUserMapper.toDto(mockUser, s3Key)).thenReturn(mockUserResponseDTO);
        when(mockUserMapper.toBuilder(mockUser)).thenReturn(mockUserBuilder);
        when(mockUserMapper.toEntity(mockUserRequestDTO, s3Key)).thenReturn(mockUser);
        when(mockUserMapper.toDoc(mockUser)).thenReturn(mockUserDoc);

        // Act
        UserResponseDTO dtoResult = context.toDto(mockUser, s3Key);
        UserBuilder builderResult = context.toBuilder(mockUser);
        User entityResult = context.toEntity(mockUserRequestDTO, s3Key);
        UserDoc docResult = context.toUserDoc(mockUser);

        // Assert
        assertSame(mockUserResponseDTO, dtoResult);
        assertSame(mockUserBuilder, builderResult);
        assertSame(mockUser, entityResult);
        assertSame(mockUserDoc, docResult);

        verify(mockUserMapper).toDto(mockUser, s3Key);
        verify(mockUserMapper).toBuilder(mockUser);
        verify(mockUserMapper).toEntity(mockUserRequestDTO, s3Key);
        verify(mockUserMapper).toDoc(mockUser);
    }

    /**
     * Tests that changing the mapper at runtime works as expected.
     */
    @Test
    @DisplayName("setUserMapper: should allow changing mapper during runtime")
    void setUserMapper_shouldAllowChangingMapper() {
        // Arrange
        UserMapper secondMapper = mock(UserMapper.class);
        context.setUserMapper(mockUserMapper);

        when(mockUserMapper.toDto(mockUser, "test")).thenReturn(mockUserResponseDTO);

        UserResponseDTO secondDto = mock(UserResponseDTO.class);
        when(secondMapper.toDto(mockUser, "test")).thenReturn(secondDto);

        // Act & Assert - First mapper
        UserResponseDTO firstResult = context.toDto(mockUser, "test");
        assertSame(mockUserResponseDTO, firstResult);
        verify(mockUserMapper).toDto(mockUser, "test");

        // Act & Assert - Change mapper
        context.setUserMapper(secondMapper);
        UserResponseDTO secondResult = context.toDto(mockUser, "test");
        assertSame(secondDto, secondResult);
        verify(secondMapper).toDto(mockUser, "test");
    }
}