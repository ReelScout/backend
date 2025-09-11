package click.reelscout.backend.service.implementation;

import click.reelscout.backend.dto.request.CreatePostRequestDTO;
import click.reelscout.backend.dto.request.CreateThreadRequestDTO;
import click.reelscout.backend.dto.response.ForumPostResponseDTO;
import click.reelscout.backend.dto.response.ForumThreadResponseDTO;
import click.reelscout.backend.exception.custom.DataValidationException;
import click.reelscout.backend.mapper.definition.ForumMapper;
import click.reelscout.backend.mapper.definition.ForumReportMapper;
import click.reelscout.backend.model.jpa.Content;
import click.reelscout.backend.model.jpa.ForumPost;
import click.reelscout.backend.model.jpa.ForumThread;
import click.reelscout.backend.model.jpa.User;
import click.reelscout.backend.repository.jpa.ContentRepository;
import click.reelscout.backend.repository.jpa.ForumPostReportRepository;
import click.reelscout.backend.repository.jpa.ForumPostRepository;
import click.reelscout.backend.repository.jpa.ForumThreadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ForumServiceImplementation}.
 * Mocks repositories and mappers to test service logic in isolation.
 * <p>
 * Covers thread and post retrieval, creation, and validation logic.
 */
class ForumServiceImplementationTest {
    @Mock private ContentRepository contentRepository;
    @Mock private ForumThreadRepository threadRepository;
    @Mock private ForumPostRepository postRepository;
    @Mock private ForumPostReportRepository reportRepository;
    @Mock private ForumMapper mapper;
    @Mock private ForumReportMapper reportMapper;

    private ForumServiceImplementation service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service = new ForumServiceImplementation(contentRepository, threadRepository, postRepository, reportRepository, mapper, reportMapper);
    }

    /**
     * Tests that threads are retrieved, sorted by updatedAt descending,
     * and mapped to DTOs with correct post counts.
     */
    @Test
    void getThreadsByContent_sortsAndMaps() {
        Content content = new Content();
        ReflectionTestUtils.setField(content, "id", 1L);
        when(contentRepository.findById(1L)).thenReturn(Optional.of(content));

        User user = Mockito.mock(User.class);
        when(user.getUsername()).thenReturn("author");

        ForumThread t1 = Mockito.mock(ForumThread.class);
        ForumThread t2 = Mockito.mock(ForumThread.class);

        LocalDateTime now = LocalDateTime.now();
        when(t1.getUpdatedAt()).thenReturn(now.minusHours(1));
        when(t2.getUpdatedAt()).thenReturn(now);

        when(threadRepository.findAllByContent(content)).thenReturn(List.of(t1, t2));
        when(postRepository.countByThread(t1)).thenReturn(2L);
        when(postRepository.countByThread(t2)).thenReturn(5L);

        when(mapper.toThreadDto(t1, 2L))
                .thenReturn(new ForumThreadResponseDTO(
                        null, null, "T1", "author", null, null, 2L
                ));
        when(mapper.toThreadDto(t2, 5L))
                .thenReturn(new ForumThreadResponseDTO(
                        null, null, "T2", "author", null, null, 5L
                ));

        List<ForumThreadResponseDTO> result = service.getThreadsByContent(1L);

        assertEquals(2, result.size());
        assertEquals("T2", result.get(0).getTitle());
        assertEquals(5, result.get(0).getPostCount());
        assertEquals("T1", result.get(1).getTitle());
        assertEquals(2, result.get(1).getPostCount());
    }

    /**
     * Tests that creating a thread saves both the thread and its first post,
     * and returns the correct mapped DTO.
     */
    @Test
    void createThread_savesThreadAndFirstPost() {
        Content content = new Content();
        ReflectionTestUtils.setField(content, "id", 9L);
        when(contentRepository.findById(9L)).thenReturn(Optional.of(content));

        User author = Mockito.mock(User.class);
        when(author.getUsername()).thenReturn("u1");

        CreateThreadRequestDTO dto = new CreateThreadRequestDTO();
        dto.setTitle("Title");
        dto.setBody("First body");

        when(mapper.toThreadDto(any(ForumThread.class), anyLong()))
                .thenReturn(new ForumThreadResponseDTO(
                        null, 9L, "Title", "u1", null, null, 1L
                ));

        // Make mapper build real (non-null) entities
        ForumThread builtThread = new ForumThread();
        ForumPost builtPost = new ForumPost();
        when(mapper.toEntity(content, author, "Title")).thenReturn(builtThread);
        when(mapper.toEntity(any(ForumThread.class), any(User.class), isNull(), anyString())).thenReturn(builtPost);

        // Repositories return the same instance they receive (mimic Spring Data save)
        when(threadRepository.save(any(ForumThread.class))).thenAnswer(inv -> inv.getArgument(0));
        when(postRepository.save(any(ForumPost.class))).thenAnswer(inv -> inv.getArgument(0));

        ForumThreadResponseDTO result = service.createThread(author, 9L, dto);

        assertEquals("Title", result.getTitle());
        assertEquals(9L, result.getContentId());
        assertEquals(1, result.getPostCount());

        verify(threadRepository, times(1)).save(any(ForumThread.class));
        verify(postRepository, times(1)).save(any(ForumPost.class));
    }

    /**
     * Tests that posts in a thread are retrieved in ascending order by creation time,
     * and mapped correctly to DTOs including parent-child relationships.
     */
    @Test
    void getPostsByThread_mapsPosts() {
        ForumThread thread = new ForumThread();
        ReflectionTestUtils.setField(thread, "id", 3L);
        when(threadRepository.findById(3L)).thenReturn(Optional.of(thread));

        User user = Mockito.mock(User.class);
        when(user.getUsername()).thenReturn("u");
        when(user.getId()).thenReturn(5L);

        ForumPost p1 = Mockito.mock(ForumPost.class);
        ReflectionTestUtils.setField(p1, "id", 21L);
        ForumPost p2 = Mockito.mock(ForumPost.class);

        when(postRepository.findAllByThreadOrderByCreatedAtAsc(thread)).thenReturn(List.of(p1, p2));

        when(mapper.toPostDto(p1))
                .thenReturn(new ForumPostResponseDTO(
                        21L, 3L, 5L, "A", null, null, null
                ));
        when(mapper.toPostDto(p2))
                .thenReturn(new ForumPostResponseDTO(
                        22L, 3L, 5L, "B", 21L, null, null
                ));

        List<ForumPostResponseDTO> result = service.getPostsByThread(3L);
        assertEquals(2, result.size());
        assertNull(result.get(0).getParentId());
        assertEquals("B", result.get(1).getBody());
        assertNotNull(result.get(1).getParentId());
        assertEquals(5L, result.get(0).getAuthorId());
    }

    /**
     * Tests that creating a post with a valid parent in the same thread succeeds,
     * saving the post and returning the correct mapped DTO.
     */
    @Test
    void createPost_throwsWhenParentFromDifferentThread() {
        ForumThread thread = new ForumThread();
        ReflectionTestUtils.setField(thread, "id", 10L);
        when(threadRepository.findById(10L)).thenReturn(Optional.of(thread));

        ForumThread otherThread = new ForumThread();
        ReflectionTestUtils.setField(otherThread, "id", 11L);

        ForumPost parent = new ForumPost();
        ReflectionTestUtils.setField(parent, "thread", otherThread);
        ReflectionTestUtils.setField(parent, "id", 100L);
        when(postRepository.findById(100L)).thenReturn(Optional.of(parent));

        CreatePostRequestDTO dto = new CreatePostRequestDTO();
        dto.setBody("Hi");
        dto.setParentId(100L);

        User user = Mockito.mock(User.class);

        assertThrows(DataValidationException.class, () -> service.createPost(user, 10L, dto));
    }
}
