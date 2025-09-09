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

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.paths.chat}")
public class ChatRestController {

    private final ChatService chatService;

    @GetMapping("/dm/{username}")
    public ResponseEntity<Page<ChatMessageResponseDTO>> getDirectHistory(@PathVariable String username,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "50") int size,
                                                              Authentication authentication) {
        String me = authentication.getName();
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(size, 200));
        return ResponseEntity.ok(chatService.getDirectHistory(me, username, pageable));
    }

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
