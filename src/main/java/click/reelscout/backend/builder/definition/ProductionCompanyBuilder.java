package click.reelscout.backend.builder.definition;

import click.reelscout.backend.model.Location;
import click.reelscout.backend.model.Owner;
import click.reelscout.backend.model.ProductionCompany;

import java.util.List;

public interface ProductionCompanyBuilder extends UserBuilder<ProductionCompany, ProductionCompanyBuilder> {
    ProductionCompanyBuilder name(String name);
    ProductionCompanyBuilder location(Location location);
    ProductionCompanyBuilder website(String website);
    ProductionCompanyBuilder owners(List<Owner> owners);
}
