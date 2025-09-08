package click.reelscout.backend.mapper.implemetation;

import click.reelscout.backend.dto.response.ForumPostResponseDTO;
import click.reelscout.backend.dto.response.ForumThreadResponseDTO;
import click.reelscout.backend.mapper.definition.ForumMapper;
import click.reelscout.backend.model.jpa.ForumPost;
import click.reelscout.backend.model.jpa.ForumThread;
import org.springframework.stereotype.Component;

@Component
public class ForumMapperImplementation implements ForumMapper {
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
}
