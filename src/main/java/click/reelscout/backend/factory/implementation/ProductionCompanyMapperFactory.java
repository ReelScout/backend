package click.reelscout.backend.factory.implementation;

import click.reelscout.backend.builder.implementation.ProductionCompanyBuilderImplementation;
import click.reelscout.backend.dto.request.ProductionCompanyRequestDTO;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.factory.UserMapperFactory;
import click.reelscout.backend.mapper.definition.UserMapper;
import click.reelscout.backend.mapper.implemetation.ProductionCompanyMapperImplementation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductionCompanyMapperFactory implements UserMapperFactory {
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(UserRequestDTO userRequestDTO) {
        return userRequestDTO instanceof ProductionCompanyRequestDTO;
    }

    @Override
    public UserMapper createMapper() {
        return new ProductionCompanyMapperImplementation(new ProductionCompanyBuilderImplementation(passwordEncoder));
    }
}