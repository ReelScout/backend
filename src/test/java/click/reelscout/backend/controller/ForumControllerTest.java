package click.reelscout.backend.controller;

import click.reelscout.backend.dto.request.CreatePostRequestDTO;
import click.reelscout.backend.dto.request.CreateThreadRequestDTO;
import click.reelscout.backend.dto.response.ForumPostResponseDTO;
import click.reelscout.backend.dto.response.ForumThreadResponseDTO;
import click.reelscout.backend.model.jpa.User;
import click.reelscout.backend.service.definition.ForumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pure unit tests for ForumController (no Spring context / no MockMvc).
 * We validate delegation to ForumService and that mappings/security annotations are present.
 */
@ExtendWith(MockitoExtension.class)
class ForumControllerTest {

    @Mock
    private ForumService forumService;

    @Mock
    private CreateThreadRequestDTO createThreadRequestDTO;

    @Mock
    private CreatePostRequestDTO createPostRequestDTO;

    @Mock
    private ForumThreadResponseDTO threadResponse;

    @Mock
    private ForumPostResponseDTO postResponse;

    @Mock
    private User authenticatedUser;

    private ForumController controller;

    @BeforeEach
    void setUp() {
        controller = new ForumController(forumService);
    }

    // ---------- Behavior tests (service delegation + ResponseEntity wrapping) ----------

    @Test
    void listThreads_shouldDelegateToService_andReturnOkWithBody() {
        // Arrange
        Long contentId = 42L;
        List<ForumThreadResponseDTO> expected = List.of(threadResponse);
        when(forumService.getThreadsByContent(contentId)).thenReturn(expected);

        // Act
        ResponseEntity<List<ForumThreadResponseDTO>> response = controller.listThreads(contentId);

        // Assert
        verify(forumService).getThreadsByContent(contentId);
        assertEquals(200, response.getStatusCode().value(), "Should return HTTP 200 OK");
        assertSame(expected, response.getBody(), "Body should be the same list returned by the service");
    }

    @Test
    void createThread_shouldDelegateToService_andReturnOkWithBody() {
        // Arrange
        Long contentId = 7L;
        when(forumService.createThread(authenticatedUser, contentId, createThreadRequestDTO))
                .thenReturn(threadResponse);

        // Act
        ResponseEntity<ForumThreadResponseDTO> response =
                controller.createThread(authenticatedUser, contentId, createThreadRequestDTO);

        // Assert
        verify(forumService).createThread(authenticatedUser, contentId, createThreadRequestDTO);
        assertEquals(200, response.getStatusCode().value());
        assertSame(threadResponse, response.getBody(), "Body should be the DTO returned by service");
    }

    @Test
    void listPosts_shouldDelegateToService_andReturnOkWithBody() {
        // Arrange
        Long threadId = 99L;
        List<ForumPostResponseDTO> expected = List.of(postResponse);
        when(forumService.getPostsByThread(threadId)).thenReturn(expected);

        // Act
        ResponseEntity<List<ForumPostResponseDTO>> response = controller.listPosts(threadId);

        // Assert
        verify(forumService).getPostsByThread(threadId);
        assertEquals(200, response.getStatusCode().value());
        assertSame(expected, response.getBody());
    }

    @Test
    void createPost_shouldDelegateToService_andReturnOkWithBody() {
        // Arrange
        Long threadId = 123L;
        when(forumService.createPost(authenticatedUser, threadId, createPostRequestDTO))
                .thenReturn(postResponse);

        // Act
        ResponseEntity<ForumPostResponseDTO> response =
                controller.createPost(authenticatedUser, threadId, createPostRequestDTO);

        // Assert
        verify(forumService).createPost(authenticatedUser, threadId, createPostRequestDTO);
        assertEquals(200, response.getStatusCode().value());
        assertSame(postResponse, response.getBody());
    }

    // ---------- Annotation tests (via reflection) ----------

    @Test
    void controller_shouldHaveClassLevelRequestMapping() {
        RequestMapping mapping = ForumController.class.getAnnotation(RequestMapping.class);
        assertNotNull(mapping, "Class should be annotated with @RequestMapping");
        // Expect exactly one path element equal to "${api.paths.content}/forum"
        assertTrue(mapping.value().length > 0 || mapping.path().length > 0,
                "RequestMapping should define a value/path");
        String[] paths = mapping.value().length > 0 ? mapping.value() : mapping.path();
        assertTrue(Arrays.asList(paths).contains("${api.paths.content}/forum"),
                "Class-level mapping should be \"${api.paths.content}/forum\"");
    }

    @Test
    void listThreads_shouldHaveGetMapping_withExpectedPath() throws Exception {
        Method m = ForumController.class.getMethod("listThreads", Long.class);
        GetMapping gm = m.getAnnotation(GetMapping.class);
        assertNotNull(gm, "listThreads should be annotated with @GetMapping");
        assertTrue(Arrays.asList(gm.value()).contains("/{contentId}/threads")
                        || Arrays.asList(gm.path()).contains("/{contentId}/threads"),
                "Path should be \"/{contentId}/threads\"");
    }

    @Test
    void createThread_shouldHavePostMapping_andPreAuthorizeIsAuthenticated() throws Exception {
        Method m = ForumController.class.getMethod(
                "createThread",
                click.reelscout.backend.model.jpa.User.class,
                Long.class,
                click.reelscout.backend.dto.request.CreateThreadRequestDTO.class
        );

        PostMapping pm = m.getAnnotation(PostMapping.class);
        assertNotNull(pm, "createThread should be annotated with @PostMapping");
        assertTrue(Arrays.asList(pm.value()).contains("/{contentId}/threads")
                        || Arrays.asList(pm.path()).contains("/{contentId}/threads"),
                "Path should be \"/{contentId}/threads\"");

        PreAuthorize pa = m.getAnnotation(PreAuthorize.class);
        assertNotNull(pa, "createThread should be annotated with @PreAuthorize");
        assertEquals("isAuthenticated()", pa.value(),
                "@PreAuthorize should require isAuthenticated()");
    }

    @Test
    void listPosts_shouldHaveGetMapping_withExpectedPath() throws Exception {
        Method m = ForumController.class.getMethod("listPosts", Long.class);
        GetMapping gm = m.getAnnotation(GetMapping.class);
        assertNotNull(gm, "listPosts should be annotated with @GetMapping");
        assertTrue(Arrays.asList(gm.value()).contains("/threads/{threadId}/posts")
                        || Arrays.asList(gm.path()).contains("/threads/{threadId}/posts"),
                "Path should be \"/threads/{threadId}/posts\"");
    }

    @Test
    void createPost_shouldHavePostMapping_andPreAuthorizeIsAuthenticated() throws Exception {
        Method m = ForumController.class.getMethod(
                "createPost",
                click.reelscout.backend.model.jpa.User.class,
                Long.class,
                click.reelscout.backend.dto.request.CreatePostRequestDTO.class
        );

        PostMapping pm = m.getAnnotation(PostMapping.class);
        assertNotNull(pm, "createPost should be annotated with @PostMapping");
        assertTrue(Arrays.asList(pm.value()).contains("/threads/{threadId}/posts")
                        || Arrays.asList(pm.path()).contains("/threads/{threadId}/posts"),
                "Path should be \"/threads/{threadId}/posts\"");

        PreAuthorize pa = m.getAnnotation(PreAuthorize.class);
        assertNotNull(pa, "createPost should be annotated with @PreAuthorize");
        assertEquals("isAuthenticated()", pa.value(),
                "@PreAuthorize should require isAuthenticated()");
    }

    // --------- Small helper to print missing annotation info if a test fails (optional) ---------
    @SuppressWarnings("unused")
    private static String describeAnnotations(Annotation[] anns) {
        StringBuilder sb = new StringBuilder();
        for (Annotation a : anns) {
            sb.append(a.annotationType().getSimpleName()).append(" ");
        }
        return sb.toString().trim();
    }
}