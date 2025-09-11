package click.reelscout.backend.dto.response.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a single point in a time series for analytics data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeSeriesPointDTO {
    private String period;
    private long count;
}

