package click.reelscout.backend.service.implementation;

import click.reelscout.backend.builder.definition.UserBuilder;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.SearchResponseDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.factory.UserMapperFactory;
import click.reelscout.backend.factory.UserMapperFactoryRegistry;
import click.reelscout.backend.mapper.definition.UserMapper;
import click.reelscout.backend.model.elasticsearch.UserDoc;
import click.reelscout.backend.model.jpa.User;
import click.reelscout.backend.repository.jpa.UserRepository;
import click.reelscout.backend.service.definition.ContentService;
import click.reelscout.backend.service.definition.SearchService;
import click.reelscout.backend.service.definition.UserService;
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
import java.util.concurrent.ThreadPoolExecutor;

@RequiredArgsConstructor
@Service
public class SearchServiceImplementation<U extends User, B extends UserBuilder<U, B>, R extends UserRequestDTO, S extends UserResponseDTO, M extends UserMapper<U,R,S,B>> implements SearchService {
    private final UserService<U, R, S> userService;
    private final UserRepository<U> userRepository;
    private final ContentService contentService;
    private final ThreadPoolExecutor executor;
    private final UserMapperContext<U,B,R,S,M> userMapperContext;
    private final UserMapperFactoryRegistry<U,B,R,S,M, UserMapperFactory<U,B,R,S,M>> userMapperFactoryRegistry;

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public SearchResponseDTO<S> search(String query) {
        try {
            QueryStringQuery queryStringQuery = QueryBuilders.queryString()
                    .query(query)
                    .fields("*")
                    .build();

            NativeQuery searchQuery = NativeQuery.builder()
                    .withQuery(queryStringQuery._toQuery())
                    .build();

            SearchHits<UserDoc> searchHits = elasticsearchOperations.search(searchQuery, UserDoc.class);

            List<U> found = userRepository.findAllById(
                    searchHits.stream().map(SearchHit::getContent).map(UserDoc::getId).toList()
            );

            return new SearchResponseDTO<>(
                    found.stream().map(user -> {
                        userMapperContext.setUserMapper(userMapperFactoryRegistry.getMapperFor(user));

                        return userMapperContext.toDto(user, null);
                    }).toList(),
            null
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}