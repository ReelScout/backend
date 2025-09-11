package click.reelscout.backend.observer.content;

import click.reelscout.backend.dto.response.ContentResponseDTO;

/**
 * Observer for content-related domain events.
 */
public interface ContentObserver {
    /**
     * Called when new content is created.
     *
     * @param content the created content
     */
    void onContentCreated(ContentResponseDTO content);
}

