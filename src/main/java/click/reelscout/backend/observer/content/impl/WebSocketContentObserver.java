package click.reelscout.backend.observer.content.impl;

import click.reelscout.backend.dto.response.ContentResponseDTO;
import click.reelscout.backend.observer.content.ContentObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Sends a push message over STOMP when new content is created.
 * Destination: /queue/content/new
 */
@Component
@RequiredArgsConstructor
public class WebSocketContentObserver implements ContentObserver {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onContentCreated(ContentResponseDTO content) {
        messagingTemplate.convertAndSend("/queue/content/new", content);
    }
}

