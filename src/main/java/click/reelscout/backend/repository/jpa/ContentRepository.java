package click.reelscout.backend.repository.jpa;

import click.reelscout.backend.model.jpa.Content;
import click.reelscout.backend.model.jpa.ProductionCompany;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for managing Content entities.
 */
public interface ContentRepository extends JpaRepository<Content, Long> {
    /**
     * Finds all Content entities associated with a given ProductionCompany.
     *
     * @param productionCompany the ProductionCompany entity
     * @return a list of Content entities
     */
    List<Content> findAllByProductionCompany(ProductionCompany productionCompany);
}
