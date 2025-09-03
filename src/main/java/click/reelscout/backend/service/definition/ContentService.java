package click.reelscout.backend.service.definition;

import click.reelscout.backend.dto.request.ContentRequestDTO;
import click.reelscout.backend.dto.response.ContentResponseDTO;
import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.model.jpa.ProductionCompany;

import java.util.List;

public interface ContentService {
    ContentResponseDTO create(ProductionCompany authenticatedProduction, ContentRequestDTO contentRequestDTO);

    ContentResponseDTO update(ProductionCompany authenticatedProduction, Long id, ContentRequestDTO contentRequestDTO);

    List<ContentResponseDTO> getAll();

    List<ContentResponseDTO> getByProductionCompany(ProductionCompany authenticatedProduction);

    CustomResponseDTO delete(ProductionCompany authenticatedProduction, Long id);

    List<String> getContentTypes();

    List<String> getGenres();
}
