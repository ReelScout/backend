package click.reelscout.backend.repository;

import click.reelscout.backend.model.Content;
import click.reelscout.backend.model.ProductionCompany;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {

    List<Content> findAllByProductionCompany(ProductionCompany productionCompany);
}
