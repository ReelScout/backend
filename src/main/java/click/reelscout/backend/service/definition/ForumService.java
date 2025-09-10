package click.reelscout.backend.service.definition;

import click.reelscout.backend.dto.request.CreatePostRequestDTO;
import click.reelscout.backend.dto.request.CreateThreadRequestDTO;
import click.reelscout.backend.dto.request.ReportPostRequestDTO;
import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.dto.response.ForumPostResponseDTO;
import click.reelscout.backend.dto.response.ForumThreadResponseDTO;
import click.reelscout.backend.model.jpa.User;

import java.util.List;

public interface ForumService {
    List<ForumThreadResponseDTO> getThreadsByContent(Long contentId);

    ForumThreadResponseDTO createThread(User author, Long contentId, CreateThreadRequestDTO dto);

    List<ForumPostResponseDTO> getPostsByThread(Long threadId);

    ForumPostResponseDTO createPost(User author, Long threadId, CreatePostRequestDTO dto);

    void reportPost(User reporter, Long postId, ReportPostRequestDTO dto);

    CustomResponseDTO deleteThread(User moderator, Long threadId);

    CustomResponseDTO deletePost(User moderator, Long postId);
}
