package click.reelscout.backend.dto.response.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentTableRowDTO {
    private Long contentId;
    private String title;
    private long threads;
    private long posts;
    private long reports;
    private long saves;
}

