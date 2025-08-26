package click.reelscout.backend.service.implementation;

import click.reelscout.backend.dto.request.ContentRequestDTO;
import click.reelscout.backend.dto.response.ContentResponseDTO;
import click.reelscout.backend.exception.custom.EntityCreateException;
import click.reelscout.backend.exception.custom.EntityNotFoundException;
import click.reelscout.backend.exception.custom.EntityUpdateException;
import click.reelscout.backend.mapper.definition.ContentMapper;
import click.reelscout.backend.model.Content;
import click.reelscout.backend.model.ProductionCompany;
import click.reelscout.backend.repository.ContentRepository;
import click.reelscout.backend.repository.ContentTypeRepository;
import click.reelscout.backend.s3.S3Service;
import click.reelscout.backend.service.definition.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
//@Transactional(rollbackOn = Exception.class)
@Service
public class ContentServiceImplementation implements ContentService {
    private final ContentRepository contentRepository;
    private final ContentTypeRepository contentTypeRepository;
    private final S3Service s3Service;
    private final ContentMapper contentMapper;

    @Override
    public ContentResponseDTO create(ProductionCompany authenticatedProduction, ContentRequestDTO contentRequestDTO) {
        if (!contentTypeRepository.existsById(contentRequestDTO.getContentType().getName()))
            contentTypeRepository.save(contentRequestDTO.getContentType());

        try {
            String s3ImageKey = contentRequestDTO.getBase64Image() != null ? "content/" + UUID.randomUUID() : null;

            Content content = contentMapper.toBuilder(contentMapper.toEntity(contentRequestDTO, authenticatedProduction, s3ImageKey))
                    .productionCompany(authenticatedProduction)
                    .build();

            contentRepository.save(content);

            s3Service.uploadFile(s3ImageKey, contentRequestDTO.getBase64Image());

            return contentMapper.toDto(content, authenticatedProduction, contentRequestDTO.getBase64Image());
        } catch (Exception e) {
            throw new EntityCreateException(Content.class);
        }
    }

    @Override
    public ContentResponseDTO update(ProductionCompany authenticatedProduction, Long id, ContentRequestDTO contentRequestDTO) {
        if (!contentTypeRepository.existsById(contentRequestDTO.getContentType().getName()))
            contentTypeRepository.save(contentRequestDTO.getContentType());

        Content existingContent = contentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Content.class));

        String s3ImageKey = existingContent.getS3ImageKey();

        if (contentRequestDTO.getBase64Image() != null && !contentRequestDTO.getBase64Image().isEmpty()) {
            s3ImageKey = "content/" + UUID.randomUUID();
            s3Service.uploadFile(s3ImageKey, contentRequestDTO.getBase64Image());
        }

        Content updatedContent = contentMapper.toBuilder(existingContent)
                .title(contentRequestDTO.getTitle())
                .description(contentRequestDTO.getDescription())
                .contentType(contentRequestDTO.getContentType())
                .actors(contentRequestDTO.getActors())
                .directors(contentRequestDTO.getDirectors())
                .s3ImageKey(s3ImageKey)
                .trailerUrl(contentRequestDTO.getTrailerUrl())
                .productionCompany(authenticatedProduction)
                .build();

        try {
            contentRepository.save(updatedContent);

            s3Service.uploadFile(s3ImageKey, contentRequestDTO.getBase64Image());

            return contentMapper.toDto(updatedContent, authenticatedProduction, contentRequestDTO.getBase64Image());
        } catch (Exception e) {
            throw new EntityUpdateException(Content.class);
        }
    }

    @Override
    public List<ContentResponseDTO> getAll() {
        List<Content> contents = contentRepository.findAll();

        return contents.stream()
                .map(content -> contentMapper.toDto(content, content.getProductionCompany(), s3Service.getFile(content.getS3ImageKey())))
                .toList();
    }
}
