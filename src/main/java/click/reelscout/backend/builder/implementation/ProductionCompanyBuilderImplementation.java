package click.reelscout.backend.builder.implementation;

import click.reelscout.backend.builder.definition.ProductionCompanyBuilder;
import click.reelscout.backend.model.*;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
public class ProductionCompanyBuilderImplementation extends AbstractUserBuilderImplementation<ProductionCompany, ProductionCompanyBuilder> implements ProductionCompanyBuilder {
    private String name;
    private Location location;
    private String website;
    private List<Content> contents;
    private List<Owner> owners;

    @Override
    public ProductionCompanyBuilder id(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public ProductionCompanyBuilder name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public ProductionCompanyBuilder location(Location location) {
        this.location = location;
        return this;
    }

    @Override
    public ProductionCompanyBuilder website(String website) {
        this.website = website;
        return this;
    }

    @Override
    public ProductionCompanyBuilder owners(List<Owner> owners) {
        this.owners = owners;
        return this;
    }

    @Override
    public ProductionCompanyBuilder contents(List<Content> contents) {
        this.contents = contents;
        return this;
    }

    @Override
    public ProductionCompany build() {
        return new ProductionCompany(this);
    }
}
