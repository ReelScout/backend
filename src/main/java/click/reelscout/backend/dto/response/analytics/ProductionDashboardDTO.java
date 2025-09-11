package click.reelscout.backend.dto.response.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for the production dashboard analytics data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionDashboardDTO {
    // KPI cards
    private long totalContents;
    private long totalThreads;
    private long totalPosts;
    private long totalReportsLast30d;
    private long totalSaves;

    // Charts
    private List<ContentCountDTO> contentsByType;
    private List<ContentCountDTO> contentsByGenre;
    private List<TimeSeriesPointDTO> postsPerWeek;

    // Rankings
    private List<ContentTableRowDTO> topBySaves;
    private List<ContentTableRowDTO> topByForumActivity;

    // Table
    private List<ContentTableRowDTO> table;
}

