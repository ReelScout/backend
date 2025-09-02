package click.reelscout.backend.service.implementation;

import click.reelscout.backend.builder.definition.UserBuilder;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.SearchResponseDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.exception.custom.SearchException;
import click.reelscout.backend.factory.UserMapperFactory;
import click.reelscout.backend.factory.UserMapperFactoryRegistry;
import click.reelscout.backend.mapper.definition.ContentMapper;
import click.reelscout.backend.mapper.definition.UserMapper;
import click.reelscout.backend.model.elasticsearch.ContentDoc;
import click.reelscout.backend.model.elasticsearch.UserDoc;
import click.reelscout.backend.model.jpa.Content;
import click.reelscout.backend.model.jpa.User;
import click.reelscout.backend.repository.jpa.ContentRepository;
import click.reelscout.backend.repository.jpa.UserRepository;
import click.reelscout.backend.s3.S3Service;
import click.reelscout.backend.service.definition.SearchService;
import click.reelscout.backend.strategy.UserMapperContext;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

@RequiredArgsConstructor
@Service
public class SearchServiceImplementation<U extends User, B extends UserBuilder<U, B>, R extends UserRequestDTO, S extends UserResponseDTO, M extends UserMapper<U,R,S,B>> implements SearchService<S> {
    private final UserRepository<U> userRepository;
    private final UserMapperContext<U,B,R,S,M> userMapperContext;
    private final UserMapperFactoryRegistry<U,B,R,S,M, UserMapperFactory<U,B,R,S,M>> userMapperFactoryRegistry;

    private final ContentRepository contentRepository;
    private final ContentMapper contentMapper;

    private final ElasticsearchOperations elasticsearchOperations;

    private final S3Service s3Service;

    private final ThreadPoolExecutor executor;

    @Override
    public SearchResponseDTO<S> search(String query) {
        // Append wildcard to enable partial word matching (e.g., "Matt" -> "Matteo Pio")
        // This works with the Search_As_You_Type fields already configured in the model
        String wildcardQuery = query + "*";

        QueryStringQuery queryStringQuery = QueryBuilders.queryString()
                .query(wildcardQuery)
                .fields("*")
                .build();

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(queryStringQuery._toQuery())
                .build();

        // Create concurrent tasks for searching users and content
        Future<List<S>> usersFuture = executor.submit(() -> {
            SearchHits<UserDoc> searchHits = elasticsearchOperations.search(searchQuery, UserDoc.class);

            List<U> foundUsers = userRepository.findAllById(
                    searchHits.stream().map(SearchHit::getContent).map(UserDoc::getId).toList()
            );

            return foundUsers.stream().map(user -> {
                userMapperContext.setUserMapper(userMapperFactoryRegistry.getMapperFor(user));
                return userMapperContext.toDto(user, s3Service.getFile(user.getS3ImageKey()));
            }).toList();
        });

        Future<List<click.reelscout.backend.dto.response.ContentResponseDTO>> contentFuture = executor.submit(() -> {
            SearchHits<ContentDoc> contentHits = elasticsearchOperations.search(searchQuery, ContentDoc.class);

            List<Content> foundContent = contentRepository.findAllById(
                    contentHits.stream().map(SearchHit::getContent).map(ContentDoc::getId).toList()
            );

            return foundContent.stream().map(content -> contentMapper.toDto(
                    content,
                    content.getProductionCompany(),
                    s3Service.getFile(content.getS3ImageKey())
            )).toList();
        });

        try {
            // Wait for both tasks to complete and combine results
            List<S> users = usersFuture.get();
            List<click.reelscout.backend.dto.response.ContentResponseDTO> content = contentFuture.get();

            return new SearchResponseDTO<>(users, content);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new SearchException();
        }
    }
}