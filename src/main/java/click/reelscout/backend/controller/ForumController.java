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

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.paths.content}/forum")
public class ForumController {
    private final ForumService forumService;

    @GetMapping("/{contentId}/threads")
    public ResponseEntity<List<ForumThreadResponseDTO>> listThreads(@PathVariable Long contentId) {
        return ResponseEntity.ok(forumService.getThreadsByContent(contentId));
    }

    @PostMapping("/{contentId}/threads")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ForumThreadResponseDTO> createThread(@AuthenticationPrincipal User authenticatedUser,
                                                               @PathVariable Long contentId,
                                                               @Valid @RequestBody CreateThreadRequestDTO dto) {
        return ResponseEntity.ok(forumService.createThread(authenticatedUser, contentId, dto));
    }

    @GetMapping("/threads/{threadId}/posts")
    public ResponseEntity<List<ForumPostResponseDTO>> listPosts(@PathVariable Long threadId) {
        return ResponseEntity.ok(forumService.getPostsByThread(threadId));
    }

    @PostMapping("/threads/{threadId}/posts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ForumPostResponseDTO> createPost(@AuthenticationPrincipal User authenticatedUser,
                                                           @PathVariable Long threadId,
                                                           @Valid @RequestBody CreatePostRequestDTO dto) {
        return ResponseEntity.ok(forumService.createPost(authenticatedUser, threadId, dto));
    }

    @PostMapping("/posts/{postId}/report")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> reportPost(@AuthenticationPrincipal User authenticatedUser,
                                           @PathVariable Long postId,
                                           @Valid @RequestBody ReportPostRequestDTO dto) {
        forumService.reportPost(authenticatedUser, postId, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/threads/{threadId}")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<CustomResponseDTO> deleteThread(@AuthenticationPrincipal User authenticatedUser,
                                                          @PathVariable Long threadId) {
        return ResponseEntity.ok(forumService.deleteThread(authenticatedUser, threadId));
    }

    @DeleteMapping("/posts/{postId}")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<CustomResponseDTO> deletePost(@AuthenticationPrincipal User authenticatedUser,
                                                        @PathVariable Long postId) {
        return ResponseEntity.ok(forumService.deletePost(authenticatedUser, postId));
    }
}
