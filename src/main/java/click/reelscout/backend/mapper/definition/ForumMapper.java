package click.reelscout.backend.mapper.definition;

import click.reelscout.backend.dto.response.ForumPostResponseDTO;
import click.reelscout.backend.dto.response.ForumThreadResponseDTO;
import click.reelscout.backend.model.jpa.ForumPost;
import click.reelscout.backend.model.jpa.ForumThread;

public interface ForumMapper {
    ForumThreadResponseDTO toThreadDto(ForumThread thread, long postCount);

    ForumPostResponseDTO toPostDto(ForumPost post);
}

