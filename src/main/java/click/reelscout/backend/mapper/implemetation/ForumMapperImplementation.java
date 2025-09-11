package click.reelscout.backend.mapper.implemetation;

import click.reelscout.backend.builder.definition.ForumPostBuilder;
import click.reelscout.backend.builder.definition.ForumThreadBuilder;
import click.reelscout.backend.dto.response.ForumPostResponseDTO;
import click.reelscout.backend.dto.response.ForumThreadResponseDTO;
import click.reelscout.backend.mapper.definition.ForumMapper;
import click.reelscout.backend.model.jpa.Content;
import click.reelscout.backend.model.jpa.ForumPost;
import click.reelscout.backend.model.jpa.ForumThread;
import click.reelscout.backend.model.jpa.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ForumMapperImplementation implements ForumMapper {
    private final ForumThreadBuilder threadBuilder;
    private final ForumPostBuilder postBuilder;

    /** {@inheritDoc} */
    @Override
    public ForumThreadResponseDTO toThreadDto(ForumThread thread, long postCount) {
        return new ForumThreadResponseDTO(
                thread.getId(),
                thread.getContent().getId(),
                thread.getTitle(),
                thread.getCreatedBy().getUsername(),
                thread.getCreatedAt(),
                thread.getUpdatedAt(),
                postCount
        );
    }

    /** {@inheritDoc} */
    @Override
    public ForumPostResponseDTO toPostDto(ForumPost post) {
        return new ForumPostResponseDTO(
                post.getId(),
                post.getThread().getId(),
                post.getAuthor().getId(),
                post.getBody(),
                post.getParent() != null ? post.getParent().getId() : null,
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }

    /** {@inheritDoc} */
    @Override
    public ForumThreadBuilder toBuilder(ForumThread thread) {
        return threadBuilder
                .id(thread.getId())
                .content(thread.getContent())
                .title(thread.getTitle())
                .createdBy(thread.getCreatedBy())
                .createdAt(thread.getCreatedAt())
                .updatedAt(thread.getUpdatedAt());
    }

    /** {@inheritDoc} */
    @Override
    public ForumPostBuilder toBuilder(ForumPost post) {
        return postBuilder
                .id(post.getId())
                .thread(post.getThread())
                .author(post.getAuthor())
                .parent(post.getParent())
                .body(post.getBody())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt());
    }

    /** {@inheritDoc} */
    @Override
    public ForumThread toEntity(Content content, User author, String title) {
        return threadBuilder
                .id(null)
                .content(content)
                .title(title)
                .createdBy(author)
                .createdAt(null)
                .updatedAt(null)
                .build();
    }

    /** {@inheritDoc} */
    @Override
    public ForumPost toEntity(ForumThread thread, User author, ForumPost parent, String body) {
        return postBuilder
                .id(null)
                .thread(thread)
                .author(author)
                .parent(parent)
                .body(body)
                .createdAt(null)
                .updatedAt(null)
                .build();
    }
}
