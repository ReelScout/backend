package click.reelscout.backend.factory.implementation;

import click.reelscout.backend.builder.definition.ProductionCompanyBuilder;
import click.reelscout.backend.dto.request.ProductionCompanyRequestDTO;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.ProductionCompanyResponseDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.factory.UserMapperFactory;
import click.reelscout.backend.mapper.definition.UserMapper;
import click.reelscout.backend.mapper.implemetation.ProductionCompanyMapperImplementation;
import click.reelscout.backend.model.ProductionCompany;
import click.reelscout.backend.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@SuppressWarnings("rawtypes")
@Component
@RequiredArgsConstructor
public class ProductionCompanyMapperFactory implements UserMapperFactory {
    private final ProductionCompanyBuilder productionCompanyBuilder;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(UserRequestDTO userRequestDTO) {
        return userRequestDTO instanceof ProductionCompanyRequestDTO;
    }

    @Override
    public boolean supports(UserResponseDTO userResponseDTO) {
        return userResponseDTO instanceof ProductionCompanyResponseDTO;
    }

    @Override
    public boolean supports(User user) {
        return user instanceof ProductionCompany;
    }

    @Override
    public UserMapper createMapper() {
        return new ProductionCompanyMapperImplementation(productionCompanyBuilder, passwordEncoder);
    }
}