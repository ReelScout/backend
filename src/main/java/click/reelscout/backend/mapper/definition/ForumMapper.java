package click.reelscout.backend.mapper.definition;

import click.reelscout.backend.builder.definition.ForumPostBuilder;
import click.reelscout.backend.builder.definition.ForumThreadBuilder;
import click.reelscout.backend.dto.response.ForumPostResponseDTO;
import click.reelscout.backend.dto.response.ForumThreadResponseDTO;
import click.reelscout.backend.model.jpa.Content;
import click.reelscout.backend.model.jpa.ForumPost;
import click.reelscout.backend.model.jpa.ForumThread;
import click.reelscout.backend.model.jpa.User;

public interface ForumMapper {
    // DTO mapping
    ForumThreadResponseDTO toThreadDto(ForumThread thread, long postCount);
    ForumPostResponseDTO toPostDto(ForumPost post);

    // Builders
    ForumThreadBuilder toBuilder(ForumThread thread);
    ForumPostBuilder toBuilder(ForumPost post);

    // Entities
    ForumThread toEntity(Content content, User author, String title);
    ForumPost toEntity(ForumThread thread, User author, ForumPost parent, String body);
}
