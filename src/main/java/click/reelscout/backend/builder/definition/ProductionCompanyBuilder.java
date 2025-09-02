package click.reelscout.backend.builder.definition;

import click.reelscout.backend.model.jpa.Content;
import click.reelscout.backend.model.jpa.Location;
import click.reelscout.backend.model.jpa.Owner;
import click.reelscout.backend.model.jpa.ProductionCompany;

import java.util.List;

public interface ProductionCompanyBuilder extends UserBuilder<ProductionCompany, ProductionCompanyBuilder> {
    ProductionCompanyBuilder name(String name);
    ProductionCompanyBuilder location(Location location);
    ProductionCompanyBuilder website(String website);
    ProductionCompanyBuilder contents(List<Content> contents);
    ProductionCompanyBuilder owners(List<Owner> owners);
}
