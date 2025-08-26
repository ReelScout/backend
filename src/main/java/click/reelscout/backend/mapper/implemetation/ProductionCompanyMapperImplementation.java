package click.reelscout.backend.mapper.implemetation;

import click.reelscout.backend.builder.definition.ProductionCompanyBuilder;
import click.reelscout.backend.dto.request.ProductionCompanyRequestDTO;
import click.reelscout.backend.dto.response.ProductionCompanyResponseDTO;
import click.reelscout.backend.mapper.definition.ProductionCompanyMapper;
import click.reelscout.backend.model.ProductionCompany;
import click.reelscout.backend.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductionCompanyMapperImplementation implements ProductionCompanyMapper {
    private final ProductionCompanyBuilder productionCompanyBuilder;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ProductionCompanyResponseDTO toDto(ProductionCompany productionCompany, String base64Image) {
        return new ProductionCompanyResponseDTO(productionCompany.getId(), productionCompany.getName(), productionCompany.getLocation(), productionCompany.getWebsite(), productionCompany.getOwners(), productionCompany.getUsername(), productionCompany.getEmail(), productionCompany.getRole(), base64Image);
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
                .role(productionCompany.getRole())
                .s3ImageKey(productionCompany.getS3ImageKey())
                .contents(productionCompany.getContents());
    }

    @Override
    public ProductionCompany toEntity(ProductionCompanyRequestDTO productionCompanyRequestDTO, String s3ImageKey) {
        return productionCompanyBuilder
                .name(productionCompanyRequestDTO.getName())
                .location(productionCompanyRequestDTO.getLocation())
                .website(productionCompanyRequestDTO.getWebsite())
                .owners(productionCompanyRequestDTO.getOwners())
                .username(productionCompanyRequestDTO.getUsername())
                .email(productionCompanyRequestDTO.getEmail())
                .password(passwordEncoder.encode(productionCompanyRequestDTO.getPassword()))
                .role(Role.PRODUCTION_COMPANY)
                .s3ImageKey(s3ImageKey)
                .build();
    }
}
