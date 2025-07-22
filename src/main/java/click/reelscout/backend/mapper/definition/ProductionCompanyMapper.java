package click.reelscout.backend.mapper.definition;

import click.reelscout.backend.builder.definition.ProductionCompanyBuilder;
import click.reelscout.backend.dto.request.ProductionCompanyRequestDTO;
import click.reelscout.backend.dto.response.ProductionCompanyResponseDTO;
import click.reelscout.backend.model.ProductionCompany;

public interface ProductionCompanyMapper extends UserMapper<ProductionCompany, ProductionCompanyRequestDTO, ProductionCompanyResponseDTO, ProductionCompanyBuilder>{
}
