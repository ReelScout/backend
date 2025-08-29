package click.reelscout.backend.repository;

import click.reelscout.backend.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    List<Genre> findAllByNameIgnoreCaseIn(Collection<String> name);
}
