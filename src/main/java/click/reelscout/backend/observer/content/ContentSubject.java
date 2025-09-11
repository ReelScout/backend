package click.reelscout.backend.observer.content;

import click.reelscout.backend.dto.response.ContentResponseDTO;

/**
 * Subject/Publisher interface for managing ContentObservers and dispatching events.
 */
public interface ContentSubject {
    /** Register an observer to receive content events. */
    void registerObserver(ContentObserver observer);
    /** Remove an observer from receiving content events. */
    void removeObserver(ContentObserver observer);
    /** Notify all registered observers about a new content creation event. */
    void notifyContentCreated(ContentResponseDTO content);
}

