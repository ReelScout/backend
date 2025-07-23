package click.reelscout.backend.model;

import click.reelscout.backend.builder.implementation.ProductionCompanyBuilderImplementation;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Getter
public class ProductionCompany extends User {
    @Column(nullable = false)
    private String name;

    @Embedded
    private Location location;

    private String website;

    // TODO: List<Film> films;

    @ElementCollection
    private List<Owner> owners;

    public ProductionCompany(ProductionCompanyBuilderImplementation builder) {
        super(builder.getId(), builder.getUsername(), builder.getEmail(), builder.getPassword(), builder.getRole(), builder.getS3ImageKey());
        this.name = builder.getName();
        this.location = builder.getLocation();
        this.website = builder.getWebsite();
        this.owners = builder.getOwners();
    }
}
