package click.reelscout.backend.repository.jpa;

import click.reelscout.backend.model.jpa.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing ContentType entities.
 * Provides methods for checking the existence of content types by name.
 */
public interface ContentTypeRepository extends JpaRepository<ContentType, String> {

    /**
     * Checks if a content type exists by its name, ignoring case.
     *
     * @param name the name of the content type
     * @return true if the content type exists, false otherwise
     */
    boolean existsByNameIgnoreCase(String name);
}
