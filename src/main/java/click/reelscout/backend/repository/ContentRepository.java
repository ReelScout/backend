package click.reelscout.backend.repository;

import click.reelscout.backend.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Long> {

}
