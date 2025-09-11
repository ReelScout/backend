package click.reelscout.backend.observer.content.impl;

import click.reelscout.backend.dto.response.ContentResponseDTO;
import click.reelscout.backend.observer.content.ContentObserver;
import click.reelscout.backend.observer.content.ContentSubject;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
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
    private final ContentSubject contentSubject;

    /** Register and unregister this observer with the subject */
    @PostConstruct
    public void register() {
        contentSubject.registerObserver(this);
    }

    /** Unregister this observer when the bean is destroyed */
    @PreDestroy
    public void unregister() {
        contentSubject.removeObserver(this);
    }

    /** {@inheritDoc} */
    @Override
    public void onContentCreated(ContentResponseDTO content) {
        messagingTemplate.convertAndSend("/queue/content/new", content);
    }
}
