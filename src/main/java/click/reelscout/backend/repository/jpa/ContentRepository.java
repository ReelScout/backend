package click.reelscout.backend.repository.jpa;

import click.reelscout.backend.model.jpa.Content;
import click.reelscout.backend.model.jpa.ProductionCompany;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {

    List<Content> findAllByProductionCompany(ProductionCompany productionCompany);
}
