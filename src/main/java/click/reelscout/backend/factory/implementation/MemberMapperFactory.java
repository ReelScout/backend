package click.reelscout.backend.factory.implementation;

import click.reelscout.backend.builder.implementation.MemberBuilderImplementation;
import click.reelscout.backend.dto.request.MemberRequestDTO;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.factory.UserMapperFactory;
import click.reelscout.backend.mapper.definition.UserMapper;
import click.reelscout.backend.mapper.implemetation.MemberMapperImplementation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberMapperFactory implements UserMapperFactory {
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(UserRequestDTO userRequestDTO) {
        return userRequestDTO instanceof MemberRequestDTO;
    }

    @Override
    public UserMapper createMapper() {
        return new MemberMapperImplementation(new MemberBuilderImplementation(passwordEncoder));
    }
}