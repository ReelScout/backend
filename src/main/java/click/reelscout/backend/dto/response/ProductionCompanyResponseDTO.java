package click.reelscout.backend.dto.response;

import click.reelscout.backend.model.jpa.Location;
import click.reelscout.backend.model.jpa.Owner;
import click.reelscout.backend.model.jpa.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ProductionCompanyResponseDTO extends UserResponseDTO {
    private String name;
    private Location location;
    private String website;
    private List<Owner> owners;

    public ProductionCompanyResponseDTO(
            Long id,
            String name,
            Location location,
            String website,
            List<Owner> owners,
            String username,
            String email,
            Role role,
            String base64Image
    ) {
        super(id, username, email, role, base64Image);
        this.name = name;
        this.location = location;
        this.website = website;
        this.owners = owners;
    }
}
