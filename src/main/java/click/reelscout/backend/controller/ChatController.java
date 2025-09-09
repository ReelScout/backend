package click.reelscout.backend.controller;

import click.reelscout.backend.dto.request.ChatMessageRequestDTO;
import click.reelscout.backend.dto.response.ChatMessageResponseDTO;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.service.definition.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/dm")
    public void sendDirect(ChatMessageRequestDTO inbound, Authentication authenticatedMember) {
        ChatMessageResponseDTO saved = chatService.saveDirectMessage((Member) authenticatedMember.getPrincipal(), inbound);
        messagingTemplate.convertAndSendToUser(saved.getRecipient(), "/queue/dm", saved);
        messagingTemplate.convertAndSendToUser(saved.getSender(), "/queue/dm", saved);
    }

}
