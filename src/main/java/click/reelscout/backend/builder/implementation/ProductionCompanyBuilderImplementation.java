package click.reelscout.backend.builder.implementation;

import click.reelscout.backend.builder.definition.ProductionCompanyBuilder;
import click.reelscout.backend.model.Location;
import click.reelscout.backend.model.Owner;
import click.reelscout.backend.model.ProductionCompany;
import click.reelscout.backend.model.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
@Getter
public class ProductionCompanyBuilderImplementation implements ProductionCompanyBuilder {
    private final PasswordEncoder passwordEncoder;

    private Long id;
    private String name;
    private Location location;
    private String website;
    private List<Owner> owners;
    private String username;
    private String email;
    private String password;
    private Role role;

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
    public ProductionCompanyBuilder username(String username) {
        this.username = username;
        return this;
    }

    @Override
    public ProductionCompanyBuilder email(String email) {
        this.email = email;
        return this;
    }

    @Override
    public ProductionCompanyBuilder password(String password) {
        this.password = passwordEncoder.encode(password);
        return this;
    }

    @Override
    public ProductionCompanyBuilder role(Role role) {
        this.role = role;
        return this;
    }

    @Override
    public ProductionCompany build() {
        return new ProductionCompany(this);
    }
}
