package click.reelscout.backend.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class BanUserRequestDTO {
    private String reason;
}

