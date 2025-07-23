package click.reelscout.backend.factory.implementation;

import click.reelscout.backend.builder.definition.ProductionCompanyBuilder;
import click.reelscout.backend.dto.request.ProductionCompanyRequestDTO;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.factory.UserMapperFactory;
import click.reelscout.backend.mapper.definition.UserMapper;
import click.reelscout.backend.mapper.implemetation.ProductionCompanyMapperImplementation;
import click.reelscout.backend.model.ProductionCompany;
import click.reelscout.backend.model.User;
import click.reelscout.backend.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@SuppressWarnings("rawtypes")
@Component
@RequiredArgsConstructor
public class ProductionCompanyMapperFactory implements UserMapperFactory {
    private final ProductionCompanyBuilder productionCompanyBuilder;
    private final S3Service s3Service;

    @Override
    public boolean supports(UserRequestDTO userRequestDTO) {
        return userRequestDTO instanceof ProductionCompanyRequestDTO;
    }

    @Override
    public boolean supports(User user) {
        return user instanceof ProductionCompany;
    }

    @Override
    public UserMapper createMapper() {
        return new ProductionCompanyMapperImplementation(productionCompanyBuilder, s3Service);
    }
}