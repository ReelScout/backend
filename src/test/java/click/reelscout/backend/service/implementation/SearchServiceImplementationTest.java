package click.reelscout.backend.service.implementation;

import click.reelscout.backend.dto.response.ContentResponseDTO;
import click.reelscout.backend.dto.response.SearchResponseDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.exception.custom.SearchException;
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
import click.reelscout.backend.strategy.UserMapperContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link SearchServiceImplementation}.
 * Mocks out all dependencies.
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"rawtypes", "unchecked"})
class SearchServiceImplementationTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapperContext userMapperContext;
    @Mock private UserMapperFactoryRegistry userMapperFactoryRegistry;

    @Mock private ContentRepository contentRepository;
    @Mock private ContentMapper contentMapper;

    @Mock private ElasticsearchOperations elasticsearchOperations;
    @Mock private S3Service s3Service;
    @Mock private ThreadPoolExecutor executor;

    @InjectMocks
    private SearchServiceImplementation service;

    /** Future completed successfully with the given value. */
    private static <T> Future<T> completed(T value) {
        return CompletableFuture.completedFuture(value);
    }

    /** Future completed exceptionally (wrapped in ExecutionException on get()). */
    private static <T> Future<T> failed(Throwable t) {
        return CompletableFuture.failedFuture(t);
    }

    /** Returns a SearchHits whose stream() is empty (no need to mock doc.getId()). */
    private static <T> SearchHits<T> emptyHits() {
        return mock(SearchHits.class, invocation -> {
            String name = invocation.getMethod().getName();
            if ("stream".equals(name)) {
                return Stream.<SearchHit<T>>empty();
            }
            // Provide safe defaults for common methods
            Class<?> rt = invocation.getMethod().getReturnType();
            if (rt.equals(boolean.class)) return false;
            if (rt.equals(int.class)) return 0;
            if (rt.equals(long.class)) return 0L;
            return null;
        });
    }

    /**
     * search(): when both tasks succeed, returns combined users + content.
     */
    @Test
    @DisplayName("search(): returns combined users + content when both tasks succeed")
    void search_success_combinesUsersAndContent(){
        // ES returns "some hits" – we keep it empty to avoid stubbing getId(); it doesn't matter
        when(elasticsearchOperations.search(any(NativeQuery.class), eq(UserDoc.class)))
                .thenReturn(emptyHits());
        when(elasticsearchOperations.search(any(NativeQuery.class), eq(ContentDoc.class)))
                .thenReturn(emptyHits());

        // Repositories return our entities regardless of the IDs list
        User u1 = mock(User.class); when(u1.getS3ImageKey()).thenReturn("u1k");
        User u2 = mock(User.class); when(u2.getS3ImageKey()).thenReturn("u2k");
        when(userRepository.findAllById(anyList())).thenReturn(List.of(u1, u2));

        Content c1 = mock(Content.class); when(c1.getS3ImageKey()).thenReturn("c1k");
        Content c2 = mock(Content.class); when(c2.getS3ImageKey()).thenReturn("c2k");
        when(contentRepository.findAllById(anyList())).thenReturn(List.of(c1, c2));

        // S3
        when(s3Service.getFile("u1k")).thenReturn("imgU1");
        when(s3Service.getFile("u2k")).thenReturn("imgU2");
        when(s3Service.getFile("c1k")).thenReturn("imgC1");
        when(s3Service.getFile("c2k")).thenReturn("imgC2");

        // Mapping users
        UserResponseDTO udto1 = new UserResponseDTO();
        UserResponseDTO udto2 = new UserResponseDTO();
        when(userMapperFactoryRegistry.getMapperFor(u1)).thenReturn(mock(UserMapper.class));
        when(userMapperFactoryRegistry.getMapperFor(u2)).thenReturn(mock(UserMapper.class));
        when(userMapperContext.toDto(u1, "imgU1")).thenReturn(udto1);
        when(userMapperContext.toDto(u2, "imgU2")).thenReturn(udto2);

        // Mapping content
        ContentResponseDTO cdto1 = new ContentResponseDTO();
        ContentResponseDTO cdto2 = new ContentResponseDTO();
        when(contentMapper.toDto(c1, "imgC1")).thenReturn(cdto1);
        when(contentMapper.toDto(c2, "imgC2")).thenReturn(cdto2);

        // Executor runs callables immediately
        when(executor.submit(any(Callable.class))).thenAnswer(inv -> {
            Callable<?> callable = inv.getArgument(0);
            try { return completed(callable.call()); } catch (Exception e) { return failed(e); }
        });

        // Act
        SearchResponseDTO<UserResponseDTO> result = service.search("mat");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getUsers().size());
        assertEquals(2, result.getContents().size());
        assertSame(udto1, result.getUsers().get(0));
        assertSame(udto2, result.getUsers().get(1));
        assertSame(cdto1, result.getContents().get(0));
        assertSame(cdto2, result.getContents().get(1));

        // (Optional) verify ES was queried with a NativeQuery (wildcard details not asserted)
        ArgumentCaptor<NativeQuery> captor = ArgumentCaptor.forClass(NativeQuery.class);
        verify(elasticsearchOperations, atLeastOnce()).search(captor.capture(), eq(UserDoc.class));
    }

    /**
     * search(): when one task fails, the other result is still returned.
     */
    @Test
    @DisplayName("search(): wraps failures (ExecutionException/InterruptedException) into SearchException")
    void search_failure_wrappedIntoSearchException() {
        when(executor.submit(any(Callable.class)))
                .thenReturn(completed(List.of()))                 // users task ok
                .thenReturn(failed(new RuntimeException("boom"))); // content task fails

        assertThrows(SearchException.class, () -> service.search("x"));
    }

    /**
     * search(): ensures the mapper is set per user before mapping to DTO.
     */
    @Test
    @DisplayName("search(): sets mapper per user before mapping to DTO")
    void search_setsMapperPerUser(){
        // ES results (empty to avoid stubbing getId)
        when(elasticsearchOperations.search(any(NativeQuery.class), eq(UserDoc.class)))
                .thenReturn(emptyHits());
        when(elasticsearchOperations.search(any(NativeQuery.class), eq(ContentDoc.class)))
                .thenReturn(emptyHits());

        // One user returned by the repo (IDs list is irrelevant in unit test)
        User u = mock(User.class); when(u.getS3ImageKey()).thenReturn("k");
        when(userRepository.findAllById(anyList())).thenReturn(List.of(u));
        when(contentRepository.findAllById(anyList())).thenReturn(List.of());

        when(s3Service.getFile("k")).thenReturn("img");

        UserMapper mapper = mock(UserMapper.class);
        when(userMapperFactoryRegistry.getMapperFor(u)).thenReturn(mapper);
        when(userMapperContext.toDto(u, "img")).thenReturn(new UserResponseDTO());

        when(executor.submit(any(Callable.class))).thenAnswer(inv -> {
            Callable<?> c = inv.getArgument(0);
            try { return completed(c.call()); } catch (Exception e) { return failed(e); }
        });

        SearchResponseDTO<UserResponseDTO> res = service.search("abc");

        assertEquals(1, res.getUsers().size());
        verify(userMapperContext).setUserMapper(mapper);
    }
}