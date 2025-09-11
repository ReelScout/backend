package click.reelscout.backend.repository.jpa;

import click.reelscout.backend.model.jpa.Content;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.model.jpa.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    List<Watchlist> findAllByMember(Member member);

    List<Watchlist> findAllByMemberAndContentsIsContaining(Member member, Content content);

    List<Watchlist> findAllByMemberAndIsPublic(Member member, boolean isPublic);

    long countByContentsContaining(Content content);
}
