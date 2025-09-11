package click.reelscout.backend.controller;

import click.reelscout.backend.dto.request.CreatePostRequestDTO;
import click.reelscout.backend.dto.request.CreateThreadRequestDTO;
import click.reelscout.backend.dto.request.ReportPostRequestDTO;
import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.dto.response.ForumPostResponseDTO;
import click.reelscout.backend.dto.response.ForumThreadResponseDTO;
import click.reelscout.backend.model.jpa.User;
import click.reelscout.backend.service.definition.ForumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling forum-related operations such as listing threads, creating threads and posts,
 * reporting posts, and deleting threads or posts.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.paths.content}/forum")
public class ForumController {
    private final ForumService forumService;

    /**
     * Lists the forum threads for the given content.
     *
     * @param contentId the ID of the content
     * @return a ResponseEntity containing the list of ForumThreadResponseDTO
     */
    @GetMapping("/{contentId}/threads")
    public ResponseEntity<List<ForumThreadResponseDTO>> listThreads(@PathVariable Long contentId) {
        return ResponseEntity.ok(forumService.getThreadsByContent(contentId));
    }

    /**
     * Creates a new forum thread for the specified content.
     *
     * @param authenticatedUser the authenticated user
     * @param contentId the ID of the content
     * @param dto the DTO containing thread creation details
     * @return a ResponseEntity containing the created ForumThreadResponseDTO
     */
    @PostMapping("/{contentId}/threads")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ForumThreadResponseDTO> createThread(@AuthenticationPrincipal User authenticatedUser,
                                                               @PathVariable Long contentId,
                                                               @Valid @RequestBody CreateThreadRequestDTO dto) {
        return ResponseEntity.ok(forumService.createThread(authenticatedUser, contentId, dto));
    }

    /**
     * Lists the posts for a given thread.
     *
     * @param threadId the ID of the thread
     * @return a ResponseEntity containing the list of ForumPostResponseDTO
     */
    @GetMapping("/threads/{threadId}/posts")
    public ResponseEntity<List<ForumPostResponseDTO>> listPosts(@PathVariable Long threadId) {
        return ResponseEntity.ok(forumService.getPostsByThread(threadId));
    }

    /**
     * Creates a new post in a specified thread.
     *
     * @param authenticatedUser the authenticated user
     * @param threadId the ID of the thread
     * @param dto the DTO containing post creation details
     * @return a ResponseEntity containing the created ForumPostResponseDTO
     */
    @PostMapping("/threads/{threadId}/posts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ForumPostResponseDTO> createPost(@AuthenticationPrincipal User authenticatedUser,
                                                           @PathVariable Long threadId,
                                                           @Valid @RequestBody CreatePostRequestDTO dto) {
        return ResponseEntity.ok(forumService.createPost(authenticatedUser, threadId, dto));
    }

    /**
     * Reports a post with the given report details.
     *
     * @param authenticatedUser the authenticated user
     * @param postId the ID of the post to report
     * @param dto the DTO containing report details
     * @return a ResponseEntity with an empty body
     */
    @PostMapping("/posts/{postId}/report")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> reportPost(@AuthenticationPrincipal User authenticatedUser,
                                           @PathVariable Long postId,
                                           @Valid @RequestBody ReportPostRequestDTO dto) {
        forumService.reportPost(authenticatedUser, postId, dto);
        return ResponseEntity.ok().build();
    }

    /**
     * Deletes a forum thread identified by threadId.
     *
     * @param authenticatedUser the authenticated user with moderator privileges
     * @param threadId the ID of the thread to delete
     * @return a ResponseEntity containing a CustomResponseDTO with deletion details
     */
    @DeleteMapping("/threads/{threadId}")
    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MODERATOR)")
    public ResponseEntity<CustomResponseDTO> deleteThread(@AuthenticationPrincipal User authenticatedUser,
                                                          @PathVariable Long threadId) {
        return ResponseEntity.ok(forumService.deleteThread(authenticatedUser, threadId));
    }

    /**
     * Deletes a forum post identified by postId.
     *
     * @param authenticatedUser the authenticated user with moderator privileges
     * @param postId the ID of the post to delete
     * @return a ResponseEntity containing a CustomResponseDTO with deletion details
     */
    @DeleteMapping("/posts/{postId}")
    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MODERATOR)")
    public ResponseEntity<CustomResponseDTO> deletePost(@AuthenticationPrincipal User authenticatedUser,
                                                        @PathVariable Long postId) {
        return ResponseEntity.ok(forumService.deletePost(authenticatedUser, postId));
    }
}
