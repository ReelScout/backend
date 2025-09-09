package click.reelscout.backend.observer.content;

import click.reelscout.backend.dto.response.ContentResponseDTO;

public interface ContentSubject {
    void registerObserver(ContentObserver observer);
    void removeObserver(ContentObserver observer);
    void notifyContentCreated(ContentResponseDTO content);
}

