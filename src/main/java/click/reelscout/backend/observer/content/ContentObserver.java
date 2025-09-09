package click.reelscout.backend.observer.content;

import click.reelscout.backend.dto.response.ContentResponseDTO;

/**
 * Observer for content-related domain events.
 */
public interface ContentObserver {
    void onContentCreated(ContentResponseDTO content);
}

