package click.reelscout.backend.factory.implementation;

import click.reelscout.backend.builder.definition.MemberBuilder;
import click.reelscout.backend.dto.request.MemberRequestDTO;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.MemberResponseDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.mapper.definition.UserMapper;
import click.reelscout.backend.mapper.implemetation.MemberMapperImplementation;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.model.jpa.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * Pure unit tests for MemberMapperFactory.
 * <p>
 * Notes:
 * - No Spring context is started.
 * - Dependencies (MemberBuilder, PasswordEncoder) are mocked.
 * - We verify the support predicates and the returned mapper type.
 */
@ExtendWith(MockitoExtension.class)
class MemberMapperFactoryTest {

    @Mock
    private MemberBuilder memberBuilder;

    @Mock
    private PasswordEncoder passwordEncoder;

    private MemberMapperFactory factory;

    @BeforeEach
    void setUp() {
        factory = new MemberMapperFactory(memberBuilder, passwordEncoder);
    }

    @Test
    void supports_UserRequestDTO_trueForMemberRequestDTO_falseForOthers() {
        // Positive: MemberRequestDTO
        UserRequestDTO ok = new MemberRequestDTO();
        assertTrue(factory.supports(ok), "Factory must support MemberRequestDTO");

        // Negative: an anonymous concrete UserRequestDTO (non-member)
        UserRequestDTO other = new UserRequestDTO() { };
        assertFalse(factory.supports(other), "Factory must not support non-member UserRequestDTO");
    }

    @Test
    void supports_UserResponseDTO_trueForMemberResponseDTO_falseForOthers() {
        // Positive: MemberResponseDTO
        UserResponseDTO ok = new MemberResponseDTO();
        assertTrue(factory.supports(ok), "Factory must support MemberResponseDTO");

        // Negative: a plain UserResponseDTO (if concrete) or anonymous subclass
        UserResponseDTO other = new UserResponseDTO();
        assertFalse(factory.supports(other), "Factory must not support non-member UserResponseDTO");
    }

    @Test
    void supports_User_trueForMember_falseForOthers() {
        // Positive: Member entity
        User member = new Member();
        assertTrue(factory.supports(member), "Factory must support Member entities");

        // Negative: a generic User (mocked)
        User other = mock(User.class);
        assertFalse(factory.supports(other), "Factory must not support non-member User entities");
    }

    @SuppressWarnings("rawtypes")
    @Test
    void createMapper_returnsMemberMapperImplementation() {
        UserMapper mapper = factory.createMapper();

        assertNotNull(mapper, "createMapper must not return null");
        assertInstanceOf(MemberMapperImplementation.class, mapper, "createMapper must return a MemberMapperImplementation");
    }
}