package click.reelscout.backend.repository.jpa;

import click.reelscout.backend.model.jpa.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

/**
 * Repository interface for managing Genre entities.
 */
public interface GenreRepository extends JpaRepository<Genre, Long> {
    /**
     * Finds all genres with names matching any in the provided collection, ignoring case.
     *
     * @param name Collection of genre names to search for.
     * @return List of matching Genre entities.
     */
    List<Genre> findAllByNameIgnoreCaseIn(Collection<String> name);
}
