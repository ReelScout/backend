package click.reelscout.backend.observer.content;

import click.reelscout.backend.dto.response.ContentResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Subject/Publisher that manages ContentObservers and dispatches events.
 */
@Component
public class ContentEventPublisher implements ContentSubject {

    private final List<ContentObserver> observers = new CopyOnWriteArrayList<>();

    /** {@inheritDoc} */
    @Override
    public void registerObserver(ContentObserver observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void removeObserver(ContentObserver observer) {
        observers.remove(observer);
    }

    /** {@inheritDoc} */
    @Override
    public void notifyContentCreated(ContentResponseDTO content) {
        for (ContentObserver observer : observers) {
            try {
                observer.onContentCreated(content);
            } catch (Exception ignored) {
                // avoid breaking the publish chain due to a single observer
            }
        }
    }
}
