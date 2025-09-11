package click.reelscout.backend.service.implementation;

import click.reelscout.backend.builder.implementation.ContentBuilderImplementation;
import click.reelscout.backend.builder.implementation.ForumPostBuilderImplementation;
import click.reelscout.backend.builder.implementation.ForumPostReportBuilderImplementation;
import click.reelscout.backend.builder.implementation.ForumThreadBuilderImplementation;
import click.reelscout.backend.builder.implementation.ProductionCompanyBuilderImplementation;
import click.reelscout.backend.dto.response.analytics.ContentCountDTO;
import click.reelscout.backend.dto.response.analytics.ContentTableRowDTO;
import click.reelscout.backend.dto.response.analytics.ProductionDashboardDTO;
import click.reelscout.backend.dto.response.analytics.TimeSeriesPointDTO;
import click.reelscout.backend.model.jpa.*;
import click.reelscout.backend.repository.jpa.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AnalyticsServiceImplementation}.
 * Focuses on the aggregation logic in getProductionDashboard method.
 */
@ExtendWith(MockitoExtension.class)
class AnalyticsServiceImplementationTest {

    @Mock private ContentRepository contentRepository;
    @Mock private ForumThreadRepository forumThreadRepository;
    @Mock private ForumPostRepository forumPostRepository;
    @Mock private ForumPostReportRepository forumPostReportRepository;
    @Mock private WatchlistRepository watchlistRepository;

    @InjectMocks private AnalyticsServiceImplementation service;

    private ProductionCompany company;
    private Content content1;
    private Content content2;

    @BeforeEach
    void setUp() {
        company = new ProductionCompanyBuilderImplementation()
                .id(1L)
                .username("pc1")
                .email("pc1@example.com")
                .role(Role.PRODUCTION_COMPANY)
                .name("PC One")
                .build();

        content1 = new ContentBuilderImplementation()
                .id(10L)
                .title("Movie A")
                .description("Desc A")
                .contentType(new ContentType("MOVIE"))
                .genres(List.of(new Genre("DRAMA"), new Genre("ACTION")))
                .productionCompany(company)
                .build();

        content2 = new ContentBuilderImplementation()
                .id(20L)
                .title("Series B")
                .description("Desc B")
                .contentType(new ContentType("SERIES"))
                .genres(List.of(new Genre("DRAMA")))
                .productionCompany(company)
                .build();
    }

    /**
     * Tests the getProductionDashboard method to ensure it correctly aggregates counts, time series data, and rankings.
     * Mocks repository responses to simulate various scenarios including recent and old posts/reports.
     */
    @Test
    void getProductionDashboard_aggregatesCountsTimeSeriesAndRankings() {
        // Threads
        ForumThread t1 = new ForumThreadBuilderImplementation().id(100L).content(content1).title("t1").build();
        ForumThread t2 = new ForumThreadBuilderImplementation().id(101L).content(content1).title("t2").build();
        ForumThread t3 = new ForumThreadBuilderImplementation().id(102L).content(content2).title("t3").build();

        // Posts: 3 recent, 1 old (older than 12 weeks)
        LocalDateTime now = LocalDateTime.now();
        ForumPost p1 = new ForumPostBuilderImplementation().id(200L).thread(t1).body("p1").createdAt(now.minusWeeks(1)).build();
        ForumPost p2 = new ForumPostBuilderImplementation().id(201L).thread(t2).body("p2").createdAt(now.minusWeeks(2)).build();
        ForumPost p3 = new ForumPostBuilderImplementation().id(202L).thread(t2).body("p3").createdAt(now.minusWeeks(5)).build();
        ForumPost pOld = new ForumPostBuilderImplementation().id(203L).thread(t3).body("old").createdAt(now.minusWeeks(30)).build();

        // Reports: 1 within 30 days, 1 older
        ForumPostReport rRecent = new ForumPostReportBuilderImplementation().id(300L).post(p1).reason("spam").createdAt(now.minusDays(10)).build();
        ForumPostReport rOld = new ForumPostReportBuilderImplementation().id(301L).post(p2).reason("off-topic").createdAt(now.minusDays(60)).build();

        // Repository stubs
        when(contentRepository.findAllByProductionCompany(company)).thenReturn(List.of(content1, content2));
        when(forumThreadRepository.findAllByContentIn(List.of(content1, content2))).thenReturn(List.of(t1, t2, t3));
        when(forumPostRepository.findAllByThreadIn(List.of(t1, t2, t3))).thenReturn(List.of(p1, p2, p3, pOld));
        when(forumPostReportRepository.findAllByPostIn(List.of(p1, p2, p3, pOld))).thenReturn(List.of(rRecent, rOld));
        when(watchlistRepository.countByContentsContaining(content1)).thenReturn(3L);
        when(watchlistRepository.countByContentsContaining(content2)).thenReturn(1L);

        ProductionDashboardDTO dto = service.getProductionDashboard(company);

        // KPI cards
        assertThat(dto.getTotalContents()).isEqualTo(2);
        assertThat(dto.getTotalThreads()).isEqualTo(3);
        assertThat(dto.getTotalPosts()).isEqualTo(4);
        assertThat(dto.getTotalReportsLast30d()).isEqualTo(1);
        assertThat(dto.getTotalSaves()).isEqualTo(4);

        // contentsByType
        assertThat(dto.getContentsByType())
                .extracting(ContentCountDTO::getKey, ContentCountDTO::getCount)
                .containsExactlyInAnyOrder(
                        org.assertj.core.groups.Tuple.tuple("MOVIE", 1L),
                        org.assertj.core.groups.Tuple.tuple("SERIES", 1L)
                );

        // contentsByGenre aggregated and sorted by count desc
        assertThat(dto.getContentsByGenre())
                .extracting(ContentCountDTO::getKey)
                .contains("DRAMA", "ACTION");

        // postsPerWeek covers last 12 weeks and counts only recent posts
        assertThat(dto.getPostsPerWeek()).hasSize(12);
        long totalRecentPosts = dto.getPostsPerWeek().stream().mapToLong(TimeSeriesPointDTO::getCount).sum();
        assertThat(totalRecentPosts).isEqualTo(3);

        // Rankings: top by saves and by forum activity
        assertThat(dto.getTopBySaves()).isNotEmpty();
        assertThat(dto.getTopBySaves().getFirst().getContentId()).isEqualTo(10L);

        assertThat(dto.getTopByForumActivity()).isNotEmpty();
        // content1 has 3 posts (p1,p2,p3) via t1+t2, content2 has 1 old post which should still count in totalPosts table, but ranking is by posts count overall
        ContentTableRowDTO topForum = dto.getTopByForumActivity().getFirst();
        assertThat(topForum.getContentId()).isIn(10L, 20L);

        // Table rows present for both contents
        assertThat(dto.getTable()).extracting(ContentTableRowDTO::getContentId)
                .containsExactly(10L, 20L);
    }
}

