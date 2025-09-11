package click.reelscout.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Abstract base class for entity response DTOs.
 * Contains common fields for all entity responses.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public abstract class EntityResponseDTO {
    private Long id;
}
