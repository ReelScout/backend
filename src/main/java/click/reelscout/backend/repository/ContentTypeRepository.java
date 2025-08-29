package click.reelscout.backend.repository;

import click.reelscout.backend.model.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentTypeRepository extends JpaRepository<ContentType, String> {
    boolean existsByNameIgnoreCase(String name);
}
