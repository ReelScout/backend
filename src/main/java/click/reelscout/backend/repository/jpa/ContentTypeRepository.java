package click.reelscout.backend.repository.jpa;

import click.reelscout.backend.model.jpa.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentTypeRepository extends JpaRepository<ContentType, String> {
    boolean existsByNameIgnoreCase(String name);
}
