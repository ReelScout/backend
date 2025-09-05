package click.reelscout.backend.factory;

import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.exception.custom.EntityCreateException;
import click.reelscout.backend.mapper.definition.UserMapper;
import click.reelscout.backend.model.jpa.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserMapperFactoryRegistryTest {

    // Using raw types to avoid generic boilerplate in tests
    @SuppressWarnings("rawtypes")
    private UserMapperFactory factoryA;
    @SuppressWarnings("rawtypes")
    private UserMapperFactory factoryB;

    @SuppressWarnings("rawtypes")
    private UserMapper mapperA;
    @SuppressWarnings("rawtypes")
    private UserMapper mapperB;

    @SuppressWarnings({"rawtypes"})
    private UserMapperFactoryRegistry registry;

    @BeforeEach
    @SuppressWarnings({"rawtypes", "unchecked"})
    void setUp() {
        factoryA = mock(UserMapperFactory.class);
        factoryB = mock(UserMapperFactory.class);
        mapperA = mock(UserMapper.class);
        mapperB = mock(UserMapper.class);

        // build registry with our mocked factories (order matters only for tie-break)
        registry = new UserMapperFactoryRegistry(List.of(factoryA, factoryB));
    }

    @Test
    @DisplayName("getMapperFor(R) picks the first factory that supports the request DTO")
    @SuppressWarnings({"unchecked"})
    void getMapperFor_requestDto_choosesCorrectFactory() {
        UserRequestDTO req = mock(UserRequestDTO.class);

        when(factoryA.supports(req)).thenReturn(false);
        when(factoryB.supports(req)).thenReturn(true);
        when(factoryB.createMapper()).thenReturn(mapperB);

        UserMapper<?, ?, ?, ?> result = registry.getMapperFor(req);

        assertSame(mapperB, result);
        verify(factoryA).supports(req);
        verify(factoryB).supports(req);
        verify(factoryB).createMapper();
        verifyNoMoreInteractions(factoryA, factoryB);
    }

    @Test
    @DisplayName("getMapperFor(S) picks the first factory that supports the response DTO")
    @SuppressWarnings({"unchecked"})
    void getMapperFor_responseDto_choosesCorrectFactory() {
        UserResponseDTO resDto = mock(UserResponseDTO.class);

        when(factoryA.supports(resDto)).thenReturn(true);
        when(factoryA.createMapper()).thenReturn(mapperA);

        UserMapper<?, ?, ?, ?> result = registry.getMapperFor(resDto);

        assertSame(mapperA, result);
        verify(factoryA).supports(resDto);
        verify(factoryA).createMapper();
        verifyNoMoreInteractions(factoryA);
        verifyNoInteractions(factoryB);
    }

    @Test
    @DisplayName("getMapperFor(U) picks the first factory that supports the user")
    @SuppressWarnings({"unchecked"})
    void getMapperFor_user_choosesCorrectFactory() {
        User user = mock(User.class);

        when(factoryA.supports(user)).thenReturn(false);
        when(factoryB.supports(user)).thenReturn(true);
        when(factoryB.createMapper()).thenReturn(mapperB);

        UserMapper<?, ?, ?, ?> result = registry.getMapperFor(user);

        assertSame(mapperB, result);
        verify(factoryA).supports(user);
        verify(factoryB).supports(user);
        verify(factoryB).createMapper();
        verifyNoMoreInteractions(factoryA, factoryB);
    }

    @Test
    @DisplayName("getMapperFor(*) throws EntityCreateException when no factory supports the input")
    @SuppressWarnings({"unchecked"})
    void getMapperFor_noneSupported_throws() {
        UserRequestDTO req = mock(UserRequestDTO.class);

        when(factoryA.supports(req)).thenReturn(false);
        when(factoryB.supports(req)).thenReturn(false);

        EntityCreateException ex = assertThrows(EntityCreateException.class, () -> registry.getMapperFor(req));
        assertEquals("Invalid user type", ex.getMessage());

        verify(factoryA).supports(req);
        verify(factoryB).supports(req);
        verify(factoryA, never()).createMapper();
        verify(factoryB, never()).createMapper();
    }
}