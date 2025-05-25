package click.reelscout.backend.mapper.implemetation;

import click.reelscout.backend.builder.definition.ProductionCompanyBuilder;
import click.reelscout.backend.dto.request.ProductionCompanyRequestDTO;
import click.reelscout.backend.dto.response.ProductionCompanyResponseDTO;
import click.reelscout.backend.mapper.definition.ProductionCompanyMapper;
import click.reelscout.backend.model.ProductionCompany;
import click.reelscout.backend.model.Role;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProductionCompanyMapperImplementation implements ProductionCompanyMapper {
    private final ProductionCompanyBuilder productionCompanyBuilder;

    @Override
    public ProductionCompanyResponseDTO toDto(ProductionCompany productionCompany) {
        return new ProductionCompanyResponseDTO(productionCompany.getId(), productionCompany.getName(), productionCompany.getLocation(), productionCompany.getWebsite(), productionCompany.getOwners(), productionCompany.getUsername(), productionCompany.getEmail(), productionCompany.getRole());
    }

    @Override
    public ProductionCompanyBuilder toBuilder(ProductionCompany productionCompany) {
        return productionCompanyBuilder
                .id(productionCompany.getId())
                .name(productionCompany.getName())
                .location(productionCompany.getLocation())
                .website(productionCompany.getWebsite())
                .owners(productionCompany.getOwners())
                .username(productionCompany.getUsername())
                .email(productionCompany.getEmail())
                .password(productionCompany.getPassword())
                .role(productionCompany.getRole());
    }

    @Override
    public ProductionCompany toEntity(ProductionCompanyRequestDTO productionCompanyRequestDTO) {
        return productionCompanyBuilder
                .name(productionCompanyRequestDTO.getName())
                .location(productionCompanyRequestDTO.getLocation())
                .website(productionCompanyRequestDTO.getWebsite())
                .owners(productionCompanyRequestDTO.getOwners())
                .username(productionCompanyRequestDTO.getUsername())
                .email(productionCompanyRequestDTO.getEmail())
                .password(productionCompanyRequestDTO.getPassword())
                .role(Role.PRODUCTION_COMPANY)
                .build();
    }
}
