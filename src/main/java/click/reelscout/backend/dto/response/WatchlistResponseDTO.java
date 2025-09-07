package click.reelscout.backend.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class WatchlistResponseDTO extends EntityResponseDTO {
    private String name;

    private Boolean isPublic;

    public WatchlistResponseDTO(
            Long id,
            String name,
            Boolean isPublic
    ) {
        super(id);
        this.name = name;
        this.isPublic = isPublic;
    }
}
