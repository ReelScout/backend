package click.reelscout.backend.mapper.definition;

import click.reelscout.backend.builder.definition.ForumPostBuilder;
import click.reelscout.backend.builder.definition.ForumThreadBuilder;
import click.reelscout.backend.dto.response.ForumPostResponseDTO;
import click.reelscout.backend.dto.response.ForumThreadResponseDTO;
import click.reelscout.backend.model.jpa.Content;
import click.reelscout.backend.model.jpa.ForumPost;
import click.reelscout.backend.model.jpa.ForumThread;
import click.reelscout.backend.model.jpa.User;

/**
 * Mapper interface for converting between forum-related entities, DTOs, and builders.
 */
public interface ForumMapper {
    /**
     * Converts a {@link ForumThread} entity to its corresponding DTO.
     *
     * @param thread the forum thread entity to convert
     * @param postCount the number of posts in the thread
     * @return the corresponding {@link ForumThreadResponseDTO}
     */
    ForumThreadResponseDTO toThreadDto(ForumThread thread, long postCount);

    /**
     * Converts a {@link ForumPost} entity to its corresponding DTO.
     *
     * @param post the forum post entity to convert
     * @return the corresponding {@link ForumPostResponseDTO}
     */
    ForumPostResponseDTO toPostDto(ForumPost post);

    /**
     * Converts a {@link ForumThread} entity to its builder representation.
     *
     * @param thread the forum thread entity to convert
     * @return the corresponding {@link ForumThreadBuilder}
     */
    ForumThreadBuilder toBuilder(ForumThread thread);

    /**
     * Converts a {@link ForumPost} entity to its builder representation.
     *
     * @param post the forum post entity to convert
     * @return the corresponding {@link ForumPostBuilder}
     */
    ForumPostBuilder toBuilder(ForumPost post);

    /**
     * Creates a {@link ForumThread} entity from the given content, author, and title.
     *
     * @param content the content associated with the thread
     * @param author the user who authored the thread
     * @param title the title of the thread
     * @return the created {@link ForumThread} entity
     */
    ForumThread toEntity(Content content, User author, String title);

    /**
     * Creates a {@link ForumPost} entity from the given thread, author, parent post, and body.
     *
     * @param thread the thread to which the post belongs
     * @param author the user who authored the post
     * @param parent the parent post, if any
     * @param body the body of the post
     * @return the created {@link ForumPost} entity
     */
    ForumPost toEntity(ForumThread thread, User author, ForumPost parent, String body);
}
