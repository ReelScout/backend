package click.reelscout.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for creating or updating a watchlist.
 */
@Data
public class WatchlistRequestDTO {
    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Public flag is mandatory")
    private Boolean isPublic;
}
