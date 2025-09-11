package click.reelscout.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import click.reelscout.backend.model.jpa.Location;
import click.reelscout.backend.model.jpa.Owner;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * DTO for creating or updating a Production Company.
 * Inherits common user fields from UserRequestDTO.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProductionCompanyRequestDTO extends UserRequestDTO{
    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotNull(message = "Location is mandatory")
    private Location location;

    private String website;

    @NotEmpty(message = "There has to be at least one owner")
    private List<Owner> owners;
}
