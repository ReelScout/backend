package click.reelscout.backend.service.definition;

import click.reelscout.backend.dto.request.WatchlistRequestDTO;
import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.dto.response.WatchlistResponseDTO;
import click.reelscout.backend.model.jpa.Member;

import java.util.List;

/**
 * Service definition for managing user watchlists.
 * <p>
 * Provides creation, update, deletion and querying operations for watchlists
 * and their contained content.
 */
public interface WatchlistService {
    /**
     * Create a new watchlist for a member.
     *
     * @param member              the member creating the watchlist
     * @param watchlistRequestDTO watchlist details
     * @return created {@link WatchlistResponseDTO}
     */
    WatchlistResponseDTO create(Member member, WatchlistRequestDTO watchlistRequestDTO);

    /**
     * Update an existing watchlist.
     *
     * @param member              the member performing the update
     * @param id                  the id of the watchlist to update
     * @param watchlistRequestDTO updated watchlist data
     * @return updated {@link WatchlistResponseDTO}
     */
    WatchlistResponseDTO update(Member member, Long id, WatchlistRequestDTO watchlistRequestDTO);

    /**
     * Delete a watchlist.
     *
     * @param member the member performing the deletion
     * @param id     the id of the watchlist to delete
     * @return a {@link CustomResponseDTO} describing the result
     */
    CustomResponseDTO delete(Member member, Long id);

    /**
     * Get all watchlists owned by a member.
     *
     * @param member the member whose watchlists to return
     * @return list of {@link WatchlistResponseDTO}
     */
    List<WatchlistResponseDTO> getAllByMember(Member member);

    /**
     * Add a content item to a watchlist.
     *
     * @param member      the member performing the action
     * @param watchlistId the id of the watchlist
     * @param contentId   the id of the content to add
     * @return updated {@link WatchlistResponseDTO}
     */
    WatchlistResponseDTO addContentToWatchlist(Member member, Long watchlistId, Long contentId);

    /**
     * Remove a content item from a watchlist.
     *
     * @param member      the member performing the action
     * @param watchlistId the id of the watchlist
     * @param contentId   the id of the content to remove
     * @return updated {@link WatchlistResponseDTO}
     */
    WatchlistResponseDTO removeContentFromWatchlist(Member member, Long watchlistId, Long contentId);

    /**
     * Get a watchlist by id (visibility checks applied).
     *
     * @param member the requesting member (may be null for anonymous access)
     * @param id     the id of the watchlist
     * @return {@link WatchlistResponseDTO}
     */
    WatchlistResponseDTO getById(Member member, Long id);

    /**
     * Get all watchlists for a member that contain a specific content item.
     *
     * @param member    the member whose watchlists to search
     * @param contentId the content id to filter by
     * @return list of {@link WatchlistResponseDTO}
     */
    List<WatchlistResponseDTO> getAllByMemberAndContent(Member member, Long contentId);

    /**
     * Get all public watchlists for a member by id.
     *
     * @param memberId the id of the member
     * @return list of public {@link WatchlistResponseDTO}
     */
    List<WatchlistResponseDTO> getAllPublicByMember(Long memberId);
}
