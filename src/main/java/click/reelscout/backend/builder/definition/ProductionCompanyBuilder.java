package click.reelscout.backend.builder.definition;

import click.reelscout.backend.model.jpa.Content;
import click.reelscout.backend.model.jpa.Location;
import click.reelscout.backend.model.jpa.Owner;
import click.reelscout.backend.model.jpa.ProductionCompany;

import java.util.List;

/**
 * Builder contract for creating {@link ProductionCompany} instances.
 */
public interface ProductionCompanyBuilder extends UserBuilder<ProductionCompany, ProductionCompanyBuilder> {
    /** Set the company name. */
    ProductionCompanyBuilder name(String name);
    /** Set the company location. */
    ProductionCompanyBuilder location(Location location);
    /** Set the company website. */
    ProductionCompanyBuilder website(String website);
    /** Set associated contents. */
    ProductionCompanyBuilder contents(List<Content> contents);
    /** Set company owners. */
    ProductionCompanyBuilder owners(List<Owner> owners);
}
