package click.reelscout.backend.service.implementation;

import click.reelscout.backend.dto.request.ContentRequestDTO;
import click.reelscout.backend.dto.response.ContentResponseDTO;
import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.exception.custom.EntityCreateException;
import click.reelscout.backend.exception.custom.EntityDeleteException;
import click.reelscout.backend.exception.custom.EntityNotFoundException;
import click.reelscout.backend.exception.custom.EntityUpdateException;
import click.reelscout.backend.mapper.definition.ContentMapper;
import click.reelscout.backend.model.jpa.Content;
import click.reelscout.backend.model.jpa.ContentType;
import click.reelscout.backend.model.jpa.Genre;
import click.reelscout.backend.model.jpa.ProductionCompany;
import click.reelscout.backend.repository.elasticsearch.ContentElasticRepository;
import click.reelscout.backend.repository.jpa.ContentRepository;
import click.reelscout.backend.repository.jpa.ContentTypeRepository;
import click.reelscout.backend.repository.jpa.GenreRepository;
import click.reelscout.backend.s3.S3Service;
import click.reelscout.backend.service.definition.ContentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
@Service
public class ContentServiceImplementation implements ContentService {
    private final ContentRepository contentRepository;
    private final ContentElasticRepository contentElasticRepository;
    private final ContentTypeRepository contentTypeRepository;
    private final GenreRepository genreRepository;
    private final S3Service s3Service;
    private final ContentMapper contentMapper;

    @Override
    public ContentResponseDTO create(ProductionCompany authenticatedProduction, ContentRequestDTO contentRequestDTO) {
        saveContentTypeIfNotExists(contentRequestDTO.getContentType());

        List<Genre> savedGenres = saveGenresIfNotExist(contentRequestDTO.getGenres());

        try {
            String s3ImageKey = contentRequestDTO.getBase64Image() != null ? "content/" + UUID.randomUUID() : null;

            Content content = contentMapper.toBuilder(contentMapper.toEntity(contentRequestDTO, authenticatedProduction, s3ImageKey))
                    .genres(savedGenres)
                    .build();

            Content saved = contentRepository.save(content);

            contentElasticRepository.save(contentMapper.toDoc(saved));

            s3Service.uploadFile(s3ImageKey, contentRequestDTO.getBase64Image());

            return contentMapper.toDto(content, contentRequestDTO.getBase64Image());
        } catch (Exception e) {
            throw new EntityCreateException(Content.class);
        }
    }

    @Override
    public ContentResponseDTO update(ProductionCompany authenticatedProduction, Long id, ContentRequestDTO contentRequestDTO) {
        Content existingContent = contentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Content.class));

        if (!existingContent.getProductionCompany().getId().equals(authenticatedProduction.getId())) {
            throw new EntityUpdateException("You are not authorized to update this content");
        }

        saveContentTypeIfNotExists(contentRequestDTO.getContentType());

        List<Genre> savedGenres = saveGenresIfNotExist(contentRequestDTO.getGenres());

        String s3ImageKey = null;

        if (contentRequestDTO.getBase64Image() != null && !contentRequestDTO.getBase64Image().isEmpty()) {
            if (existingContent.getS3ImageKey() == null || existingContent.getS3ImageKey().isEmpty()) {
                s3ImageKey = "content/" + UUID.randomUUID();
            } else {
                s3ImageKey = existingContent.getS3ImageKey();
            }
        } else {
            if (existingContent.getS3ImageKey() != null && !existingContent.getS3ImageKey().isEmpty()) {
                s3Service.deleteFile(existingContent.getS3ImageKey());
            }
        }

        Content updatedContent = contentMapper.toBuilder(contentMapper.toEntity(contentRequestDTO, authenticatedProduction, s3ImageKey))
                .id(id)
                .genres(savedGenres)
                .build();

        try {
            Content saved = contentRepository.save(updatedContent);
            contentElasticRepository.save(contentMapper.toDoc(saved));

            s3Service.uploadFile(s3ImageKey, contentRequestDTO.getBase64Image());

            return contentMapper.toDto(updatedContent, contentRequestDTO.getBase64Image());
        } catch (Exception e) {
            throw new EntityUpdateException(Content.class);
        }
    }

    @Override
    public List<ContentResponseDTO> getAll() {
        List<Content> contents = contentRepository.findAll();

        return contents.stream()
                .map(content -> contentMapper.toDto(content, s3Service.getFile(content.getS3ImageKey())))
                .toList();
    }

    @Override
    public List<String> getContentTypes() {
        return contentTypeRepository.findAll()
                .stream()
                .map(ContentType::getName)
                .toList();
    }

    @Override
    public List<String> getGenres() {
        return genreRepository.findAll()
                .stream()
                .map(Genre::getName)
                .toList();
    }

    @Override
    public List<ContentResponseDTO> getByProductionCompany(ProductionCompany authenticatedProduction) {
        List<Content> contents = contentRepository.findAllByProductionCompany((authenticatedProduction));

        return contents.stream()
                .map(content -> contentMapper.toDto(content, s3Service.getFile(content.getS3ImageKey())))
                .toList();
    }

    @Override
    public CustomResponseDTO delete(ProductionCompany authenticatedProduction, Long id) {
        Content toDelete = contentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Content.class));

        if (!toDelete.getProductionCompany().getId().equals(authenticatedProduction.getId())) {
            throw new EntityDeleteException("You are not authorized to delete this content");
        }

        try {
            contentRepository.delete(toDelete);

            if (toDelete.getS3ImageKey() != null)
                s3Service.deleteFile(toDelete.getS3ImageKey());
        } catch (Exception e) {
            throw new EntityDeleteException(Content.class);
        }

        return new CustomResponseDTO("Content deleted successfully");
    }

    private void saveContentTypeIfNotExists(ContentType contentType) {
        if (!contentTypeRepository.existsByNameIgnoreCase(contentType.getName()))
            contentTypeRepository.save(contentType);
    }

    private List<Genre> saveGenresIfNotExist(List<Genre> genres) {
        List<Genre> existingGenres = genreRepository.findAllByNameIgnoreCaseIn(genres.stream().map(Genre::getName).toList());

        List<Genre> toSave = genres.stream()
                .filter(genre -> !existingGenres.contains(genre))
                .toList();

        genreRepository.saveAll(toSave);

        return genreRepository.findAllByNameIgnoreCaseIn(genres.stream().map(Genre::getName).toList());
    }
}
