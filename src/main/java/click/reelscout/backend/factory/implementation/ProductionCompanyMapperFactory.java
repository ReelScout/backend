package click.reelscout.backend.factory.implementation;

import click.reelscout.backend.builder.definition.ProductionCompanyBuilder;
import click.reelscout.backend.dto.request.ProductionCompanyRequestDTO;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.ProductionCompanyResponseDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.factory.UserMapperFactory;
import click.reelscout.backend.mapper.definition.UserMapper;
import click.reelscout.backend.mapper.implemetation.ProductionCompanyMapperImplementation;
import click.reelscout.backend.model.jpa.ProductionCompany;
import click.reelscout.backend.model.jpa.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@SuppressWarnings("rawtypes")
@Component
@RequiredArgsConstructor
public class ProductionCompanyMapperFactory implements UserMapperFactory {
    private final ProductionCompanyBuilder productionCompanyBuilder;
    private final PasswordEncoder passwordEncoder;

    /** {@inheritDoc} */
    @Override
    public boolean supports(UserRequestDTO userRequestDTO) {
        return userRequestDTO instanceof ProductionCompanyRequestDTO;
    }

    /** {@inheritDoc} */
    @Override
    public boolean supports(UserResponseDTO userResponseDTO) {
        return userResponseDTO instanceof ProductionCompanyResponseDTO;
    }

    /** {@inheritDoc} */
    @Override
    public boolean supports(User user) {
        return user instanceof ProductionCompany;
    }

    /** {@inheritDoc} */
    @Override
    public UserMapper createMapper() {
        return new ProductionCompanyMapperImplementation(productionCompanyBuilder, passwordEncoder);
    }
}