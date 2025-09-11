package click.reelscout.backend.factory.implementation;

import click.reelscout.backend.builder.definition.MemberBuilder;
import click.reelscout.backend.dto.request.MemberRequestDTO;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.MemberResponseDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.factory.UserMapperFactory;
import click.reelscout.backend.mapper.definition.UserMapper;
import click.reelscout.backend.mapper.implemetation.MemberMapperImplementation;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.model.jpa.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Factory class for creating MemberMapper instances.
 * Implements the UserMapperFactory interface.
 * Uses MemberBuilder and PasswordEncoder for mapping operations.
 */
@SuppressWarnings("rawtypes")
@Component
@RequiredArgsConstructor
public class MemberMapperFactory implements UserMapperFactory {
    private final MemberBuilder memberBuilder;
    private final PasswordEncoder passwordEncoder;

    /** {@inheritDoc} */
    @Override
    public boolean supports(UserRequestDTO userRequestDTO) {
        return userRequestDTO instanceof MemberRequestDTO;
    }

    /** {@inheritDoc} */
    @Override
    public boolean supports(UserResponseDTO userResponseDTO) {
        return userResponseDTO instanceof MemberResponseDTO;
    }

    /** {@inheritDoc} */
    @Override
    public boolean supports(User user) {
        return user instanceof Member;
    }

    /** {@inheritDoc} */
    @Override
    public UserMapper createMapper() {
        return new MemberMapperImplementation(memberBuilder, passwordEncoder);
    }
}