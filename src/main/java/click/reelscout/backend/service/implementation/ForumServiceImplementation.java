package click.reelscout.backend.service.implementation;

import click.reelscout.backend.dto.request.CreatePostRequestDTO;
import click.reelscout.backend.dto.request.CreateThreadRequestDTO;
import click.reelscout.backend.dto.response.ForumPostResponseDTO;
import click.reelscout.backend.dto.response.ForumThreadResponseDTO;
import click.reelscout.backend.exception.custom.DataValidationException;
import click.reelscout.backend.exception.custom.EntityCreateException;
import click.reelscout.backend.exception.custom.EntityNotFoundException;
import click.reelscout.backend.mapper.definition.ForumMapper;
import click.reelscout.backend.mapper.definition.ForumReportMapper;
import click.reelscout.backend.dto.request.ReportPostRequestDTO;
import click.reelscout.backend.model.jpa.Content;
import click.reelscout.backend.model.jpa.ForumPost;
import click.reelscout.backend.model.jpa.ForumPostReport;
import click.reelscout.backend.model.jpa.ForumThread;
import click.reelscout.backend.model.jpa.User;
import click.reelscout.backend.repository.jpa.ContentRepository;
import click.reelscout.backend.repository.jpa.ForumPostRepository;
import click.reelscout.backend.repository.jpa.ForumPostReportRepository;
import click.reelscout.backend.repository.jpa.ForumThreadRepository;
import click.reelscout.backend.service.definition.ForumService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class ForumServiceImplementation implements ForumService {
    private final ContentRepository contentRepository;
    private final ForumThreadRepository threadRepository;
    private final ForumPostRepository postRepository;
    private final ForumPostReportRepository postReportRepository;
    private final ForumMapper forumMapper;
    private final ForumReportMapper forumReportMapper;

    @Override
    public List<ForumThreadResponseDTO> getThreadsByContent(Long contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new EntityNotFoundException(Content.class));

        return threadRepository.findAllByContent(content)
                .stream()
                .sorted(Comparator.comparing(ForumThread::getUpdatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(thread -> forumMapper.toThreadDto(thread, postRepository.countByThread(thread)))
                .toList();
    }

    @Override
    public ForumThreadResponseDTO createThread(User author, Long contentId, CreateThreadRequestDTO dto) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new EntityNotFoundException(Content.class));

        try {
            ForumThread thread = new ForumThread(content, dto.getTitle(), author);
            threadRepository.save(thread);

            ForumPost firstPost = new ForumPost(thread, author, null, dto.getBody());
            postRepository.save(firstPost);

            return forumMapper.toThreadDto(thread, 1);
        } catch (Exception e) {
            throw new EntityCreateException(ForumThread.class);
        }
    }

    @Override
    public List<ForumPostResponseDTO> getPostsByThread(Long threadId) {
        ForumThread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new EntityNotFoundException(ForumThread.class));

        return postRepository.findAllByThreadOrderByCreatedAtAsc(thread)
                .stream()
                .map(forumMapper::toPostDto)
                .toList();
    }

    @Override
    public ForumPostResponseDTO createPost(User author, Long threadId, CreatePostRequestDTO dto) {
        ForumThread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new EntityNotFoundException(ForumThread.class));

        ForumPost parent = null;
        if (dto.getParentId() != null) {
            parent = postRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException(ForumPost.class));
            if (!parent.getThread().getId().equals(thread.getId())) {
                throw new DataValidationException("Parent post does not belong to this thread");
            }
        }

        try {
            ForumPost post = new ForumPost(thread, author, parent, dto.getBody());
            postRepository.save(post);
            return forumMapper.toPostDto(post);
        } catch (Exception e) {
            throw new EntityCreateException(ForumPost.class);
        }
    }

    @Override
    public void reportPost(User reporter, Long postId, ReportPostRequestDTO dto) {
        ForumPost post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(ForumPost.class));

        if (postReportRepository.existsByPostAndReporter(post, reporter)) {
            throw new DataValidationException("You have already reported this post");
        }

        try {
            ForumPostReport report = forumReportMapper.toEntity(post, reporter, dto.getReason());
            postReportRepository.save(report);
        } catch (Exception e) {
            throw new EntityCreateException(ForumPostReport.class);
        }
    }
}
