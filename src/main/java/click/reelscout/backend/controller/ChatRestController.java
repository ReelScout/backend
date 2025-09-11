package click.reelscout.backend.controller;

import click.reelscout.backend.dto.response.ChatMessageResponseDTO;
import click.reelscout.backend.dto.response.ConversationResponseDTO;
import click.reelscout.backend.service.definition.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * REST controller for chat operations, providing endpoints for retrieving direct message history
 * and recent conversations.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.paths.chat}")
public class ChatRestController {

    private final ChatService chatService;

    /**
     * Retrieves the paginated history of direct messages between the authenticated user and the given username.
     *
     * @param username the username of the other participant in the direct message conversation
     * @param page the page number to retrieve (default is 0)
     * @param size the number of messages per page (default is 50, max 200)
     * @param authentication the authentication object containing the authenticated user's details
     * @return a {@link Page} of {@link ChatMessageResponseDTO} representing the direct message history
     */
    @GetMapping("/dm/{username}")
    public ResponseEntity<Page<ChatMessageResponseDTO>> getDirectHistory(@PathVariable String username,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "50") int size,
                                                              Authentication authentication) {
        String me = authentication.getName();
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(size, 200));
        return ResponseEntity.ok(chatService.getDirectHistory(me, username, pageable));
    }

    /**
     * Retrieves the most recent direct conversations for the authenticated user.
     *
     * @param size the maximum number of recent conversations to retrieve (default is 20, clamped between 1 and 100)
     * @param authentication the authentication object containing the authenticated user's details
     * @return a list of {@link ConversationResponseDTO} representing the recent direct conversations
     */
    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationResponseDTO>> getRecentConversations(
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication
    ) {
        String me = authentication.getName();
        int s = Math.clamp(size, 1, 100);
        return ResponseEntity.ok(chatService.getRecentDirectConversations(me, s));
    }
}
