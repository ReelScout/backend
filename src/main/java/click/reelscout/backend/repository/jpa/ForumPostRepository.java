package click.reelscout.backend.repository.jpa;

import click.reelscout.backend.model.jpa.ForumPost;
import click.reelscout.backend.model.jpa.ForumThread;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {
    List<ForumPost> findAllByThreadOrderByCreatedAtAsc(ForumThread thread);

    long countByThread(ForumThread thread);

    void deleteByThread(ForumThread thread);

    List<ForumPost> findAllByParent(ForumPost parent);
}
