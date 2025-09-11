package click.reelscout.backend.dto.response.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeSeriesPointDTO {
    private String period;
    private long count;
}

