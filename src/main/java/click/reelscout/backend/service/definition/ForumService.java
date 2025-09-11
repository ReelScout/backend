package click.reelscout.backend.service.definition;

import click.reelscout.backend.dto.request.CreatePostRequestDTO;
import click.reelscout.backend.dto.request.CreateThreadRequestDTO;
import click.reelscout.backend.dto.request.ReportPostRequestDTO;
import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.dto.response.ForumPostResponseDTO;
import click.reelscout.backend.dto.response.ForumThreadResponseDTO;
import click.reelscout.backend.model.jpa.User;

import java.util.List;

/**
 * Service definition for forum functionality tied to content.
 * <p>
 * Provides operations to list and create threads and posts, report posts,
 * and moderation actions for deleting threads and posts.
 */
public interface ForumService {
    /**
     * Retrieve forum threads associated with a content item.
     *
     * @param contentId the id of the content
     * @return list of {@link ForumThreadResponseDTO} for the content
     */
    List<ForumThreadResponseDTO> getThreadsByContent(Long contentId);

    /**
     * Create a forum thread for a content item.
     *
     * @param author    the user creating the thread
     * @param contentId the id of the content
     * @param dto       thread creation DTO
     * @return the created thread as {@link ForumThreadResponseDTO}
     */
    ForumThreadResponseDTO createThread(User author, Long contentId, CreateThreadRequestDTO dto);

    /**
     * Retrieve posts belonging to a thread.
     *
     * @param threadId the id of the thread
     * @return list of {@link ForumPostResponseDTO} for the thread
     */
    List<ForumPostResponseDTO> getPostsByThread(Long threadId);

    /**
     * Create a post in a thread.
     *
     * @param author   the user creating the post
     * @param threadId the id of the thread
     * @param dto      post creation DTO
     * @return the created post as {@link ForumPostResponseDTO}
     */
    ForumPostResponseDTO createPost(User author, Long threadId, CreatePostRequestDTO dto);

    /**
     * Report a forum post for moderation.
     *
     * @param reporter the user reporting the post
     * @param postId   the id of the post to report
     * @param dto      report details
     */
    void reportPost(User reporter, Long postId, ReportPostRequestDTO dto);

    /**
     * Delete a thread (moderation action).
     *
     * @param moderator the moderator performing the deletion
     * @param threadId  the id of the thread to delete
     * @return a {@link CustomResponseDTO} describing the result
     */
    CustomResponseDTO deleteThread(User moderator, Long threadId);

    /**
     * Delete a post (moderation action).
     *
     * @param moderator the moderator performing the deletion
     * @param postId    the id of the post to delete
     * @return a {@link CustomResponseDTO} describing the result
     */
    CustomResponseDTO deletePost(User moderator, Long postId);
}
