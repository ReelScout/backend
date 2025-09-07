package click.reelscout.backend.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class WatchlistWithContentsResponseDTO extends WatchlistResponseDTO{
    private List<ContentResponseDTO> contents;

    public WatchlistWithContentsResponseDTO(
            Long id,
            String name,
            Boolean isPublic,
            List<ContentResponseDTO> contents
    ) {
        super(id, name, isPublic);
        this.contents = contents;
    }
}
