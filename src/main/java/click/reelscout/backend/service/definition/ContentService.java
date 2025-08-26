package click.reelscout.backend.service.definition;

import click.reelscout.backend.dto.request.ContentRequestDTO;
import click.reelscout.backend.dto.response.ContentResponseDTO;
import click.reelscout.backend.model.ProductionCompany;

import java.util.List;

public interface ContentService {
    ContentResponseDTO create(ProductionCompany authenticatedProduction, ContentRequestDTO contentRequestDTO);

    ContentResponseDTO update(ProductionCompany authenticatedProduction, Long id, ContentRequestDTO contentRequestDTO);

    List<ContentResponseDTO> getAll();
}
