package click.reelscout.backend.repository.jpa;

import click.reelscout.backend.model.jpa.Content;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.model.jpa.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for managing Watchlist entities.
 * Provides methods for retrieving and managing watchlists.
 */
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {

    /**
     * Retrieves all watchlists for a specific member.
     *
     * @param member the member whose watchlists are to be retrieved
     * @return a list of watchlists for the member
     */
    List<Watchlist> findAllByMember(Member member);

    /**
     * Finds watchlists for a member that contain specific content.
     *
     * @param member the member whose watchlists are to be searched
     * @param content the content to search for
     * @return a list of matching watchlists
     */
    List<Watchlist> findAllByMemberAndContentsIsContaining(Member member, Content content);

    /**
     * Retrieves public watchlists for a specific member.
     *
     * @param member the member whose public watchlists are to be retrieved
     * @param isPublic whether the watchlists are public
     * @return a list of public watchlists for the member
     */
    List<Watchlist> findAllByMemberAndIsPublic(Member member, boolean isPublic);

    /**
     * Counts the number of watchlists containing specific content.
     *
     * @param content the content to count
     * @return the number of watchlists containing the content
     */
    long countByContentsContaining(Content content);
}
