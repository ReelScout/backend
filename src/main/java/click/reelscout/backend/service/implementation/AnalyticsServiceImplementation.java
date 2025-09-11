package click.reelscout.backend.service.implementation;

import click.reelscout.backend.dto.response.analytics.*;
import click.reelscout.backend.model.jpa.*;
import click.reelscout.backend.repository.jpa.*;
import click.reelscout.backend.service.definition.AnalyticsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class AnalyticsServiceImplementation implements AnalyticsService {

    private final ContentRepository contentRepository;
    private final ForumThreadRepository forumThreadRepository;
    private final ForumPostRepository forumPostRepository;
    private final ForumPostReportRepository forumPostReportRepository;
    private final WatchlistRepository watchlistRepository;

    /** {@inheritDoc} */
    @Override
    public ProductionDashboardDTO getProductionDashboard(ProductionCompany productionCompany) {
        List<Content> contents = Optional.ofNullable(contentRepository.findAllByProductionCompany(productionCompany))
                .orElse(Collections.emptyList());

        // Threads and Posts in one shot
        List<ForumThread> threads = contents.isEmpty()
                ? Collections.emptyList()
                : Optional.ofNullable(forumThreadRepository.findAllByContentIn(contents)).orElse(Collections.emptyList());

        List<ForumPost> posts = threads.isEmpty()
                ? Collections.emptyList()
                : Optional.ofNullable(forumPostRepository.findAllByThreadIn(threads)).orElse(Collections.emptyList());

        // Reports: load all for posts (enables per-content and last30d)
        List<ForumPostReport> reports = posts.isEmpty()
                ? Collections.emptyList()
                : Optional.ofNullable(forumPostReportRepository.findAllByPostIn(posts)).orElse(Collections.emptyList());

        // KPI cards
        long totalContents = contents.size();
        long totalThreads = threads.size();
        long totalPosts = posts.size();
        LocalDateTime from30d = LocalDateTime.now().minusDays(30);
        long totalReportsLast30d = reports.stream().filter(r -> !r.getCreatedAt().isBefore(from30d)).count();
        long totalSaves = contents.stream().mapToLong(watchlistRepository::countByContentsContaining).sum();

        // Charts: contents by type
        Map<String, Long> byType = contents.stream()
                .collect(Collectors.groupingBy(c -> c.getContentType().getName(), Collectors.counting()));

        List<ContentCountDTO> contentsByType = byType.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new ContentCountDTO(e.getKey(), e.getValue()))
                .toList();

        // Charts: contents by genre
        Map<String, Long> byGenre = new HashMap<>();
        contents.forEach(c -> Optional.ofNullable(c.getGenres()).orElse(Collections.emptyList())
                .forEach(g -> byGenre.merge(g.getName(), 1L, Long::sum)));

        List<ContentCountDTO> contentsByGenre = byGenre.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .map(e -> new ContentCountDTO(e.getKey(), e.getValue()))
                .toList();

        // Time series: posts per ISO week (last 12 weeks)
        LocalDate start = LocalDate.now().minusWeeks(12).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        DateTimeFormatter fmt = DateTimeFormatter.ISO_DATE;
        Map<String, Long> postsByWeek = posts.stream()
                .filter(p -> p.getCreatedAt() != null && !p.getCreatedAt().toLocalDate().isBefore(start))
                .collect(Collectors.groupingBy(p -> weekKey(p.getCreatedAt().toLocalDate()), Collectors.counting()));

        // Fill missing weeks
        List<TimeSeriesPointDTO> postsPerWeek = new ArrayList<>();
        LocalDate cursor = start;
        for (int i = 0; i < 12; i++) {
            String key = fmt.format(cursor);
            postsPerWeek.add(new TimeSeriesPointDTO(key, postsByWeek.getOrDefault(key, 0L)));
            cursor = cursor.plusWeeks(1);
        }

        // Per-content aggregations for rankings and table
        Map<Long, Long> threadCountByContent = threads.stream()
                .collect(Collectors.groupingBy(t -> t.getContent().getId(), Collectors.counting()));

        Map<Long, Long> postCountByContent = new HashMap<>();
        posts.forEach(p -> postCountByContent.merge(p.getThread().getContent().getId(), 1L, Long::sum));

        Map<Long, Long> reportCountByContent = new HashMap<>();
        reports.forEach(r -> reportCountByContent.merge(r.getPost().getThread().getContent().getId(), 1L, Long::sum));

        Map<Long, Long> saveCountByContent = new HashMap<>();
        contents.forEach(c -> saveCountByContent.put(c.getId(), watchlistRepository.countByContentsContaining(c)));

        // Table rows
        List<ContentTableRowDTO> table = contents.stream().map(c -> new ContentTableRowDTO(
                c.getId(),
                c.getTitle(),
                threadCountByContent.getOrDefault(c.getId(), 0L),
                postCountByContent.getOrDefault(c.getId(), 0L),
                reportCountByContent.getOrDefault(c.getId(), 0L),
                saveCountByContent.getOrDefault(c.getId(), 0L)
        )).sorted(Comparator.comparing(ContentTableRowDTO::getContentId)).toList();

        // Rankings
        List<ContentTableRowDTO> topBySaves = table.stream()
                .sorted(Comparator.comparingLong(ContentTableRowDTO::getSaves).reversed())
                .limit(5)
                .toList();

        List<ContentTableRowDTO> topByForumActivity = table.stream()
                .sorted(Comparator.comparingLong(ContentTableRowDTO::getPosts).reversed())
                .limit(5)
                .toList();

        return new ProductionDashboardDTO(
                totalContents,
                totalThreads,
                totalPosts,
                totalReportsLast30d,
                totalSaves,
                contentsByType,
                contentsByGenre,
                postsPerWeek,
                topBySaves,
                topByForumActivity,
                table
        );
    }

    private String weekKey(LocalDate date) {
        LocalDate monday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return monday.format(DateTimeFormatter.ISO_DATE);
    }
}
