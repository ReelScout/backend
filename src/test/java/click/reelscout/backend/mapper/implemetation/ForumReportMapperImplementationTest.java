package click.reelscout.backend.mapper.implemetation;

import click.reelscout.backend.builder.definition.ForumPostReportBuilder;
import click.reelscout.backend.model.jpa.ForumPost;
import click.reelscout.backend.model.jpa.ForumPostReport;
import click.reelscout.backend.model.jpa.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ForumReportMapperImplementation}.
 * The builder is mocked to isolate the mapper logic.
 */
@ExtendWith(MockitoExtension.class)
class ForumReportMapperImplementationTest {

    @Mock
    private ForumPostReportBuilder reportBuilder;

    private ForumReportMapperImplementation mapper;

    @BeforeEach
    void setUp() {
        // The mapper receives the (mocked) builder via constructor injection
        mapper = new ForumReportMapperImplementation(reportBuilder);

        // Default fluent stubbing: every setter returns the same builder to allow chaining
        when(reportBuilder.id(any())).thenReturn(reportBuilder);
        when(reportBuilder.post(any())).thenReturn(reportBuilder);
        when(reportBuilder.reporter(any())).thenReturn(reportBuilder);
        when(reportBuilder.reason(any())).thenReturn(reportBuilder);
        when(reportBuilder.createdAt(any())).thenReturn(reportBuilder);
    }

    /** Tests for toBuilder method */
    @Test
    void toBuilder_copiesAllFieldsFromReport_andReturnsSameBuilder() {
        // Arrange
        ForumPostReport report = mock(ForumPostReport.class);
        Long id = 42L;
        ForumPost post = mock(ForumPost.class);
        User reporter = mock(User.class);
        String reason = "spam/abuse";
        LocalDateTime createdAt = LocalDateTime.parse("2024-05-01T12:34:56");

        // Stub getters used by the mapper
        when(report.getId()).thenReturn(id);
        when(report.getPost()).thenReturn(post);
        when(report.getReporter()).thenReturn(reporter);
        when(report.getReason()).thenReturn(reason);
        when(report.getCreatedAt()).thenReturn(createdAt);

        // Act
        ForumPostReportBuilder returned = mapper.toBuilder(report);

        // Assert
        assertThat(returned).isSameAs(reportBuilder);
        verify(reportBuilder).id(id);
        verify(reportBuilder).post(post);
        verify(reportBuilder).reporter(reporter);
        verify(reportBuilder).reason(reason);
        verify(reportBuilder).createdAt(createdAt);
        verify(reportBuilder, never()).build();
    }

    /** Tests for toEntity method */
    @Test
    void toEntity_buildsReport_withGivenInputs_andNullIdAndCreatedAt() {
        // Arrange
        ForumPost post = mock(ForumPost.class);
        User reporter = mock(User.class);
        String reason = "offensive language";
        ForumPostReport built = mock(ForumPostReport.class);

        // Final build() must return our prepared entity
        when(reportBuilder.build()).thenReturn(built);

        // Act
        ForumPostReport result = mapper.toEntity(post, reporter, reason);

        // Assert
        // The mapper must return exactly what builder.build() returns
        assertThat(result).isSameAs(built);

        // Verify the fluent calls in the exact order isn't necessary, but values must match
        verify(reportBuilder).id(null);          // id must be null for a new entity
        verify(reportBuilder).post(post);        // provided post is propagated
        verify(reportBuilder).reporter(reporter);// provided reporter is propagated
        verify(reportBuilder).reason(reason);    // provided reason is propagated

        // createdAt must be explicitly set to null (so persistence layer can populate it)
        ArgumentCaptor<LocalDateTime> createdAtCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(reportBuilder).createdAt(createdAtCaptor.capture());
        assertThat(createdAtCaptor.getValue()).isNull();

        // build() must be called once to get the entity
        verify(reportBuilder).build();
        verifyNoMoreInteractions(reportBuilder);
    }
}