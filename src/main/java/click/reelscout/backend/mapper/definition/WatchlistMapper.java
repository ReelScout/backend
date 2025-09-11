package click.reelscout.backend.mapper.definition;

import click.reelscout.backend.builder.definition.WatchlistBuilder;
import click.reelscout.backend.dto.request.WatchlistRequestDTO;
import click.reelscout.backend.dto.response.ContentResponseDTO;
import click.reelscout.backend.dto.response.WatchlistResponseDTO;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.model.jpa.Watchlist;

import java.util.List;

/**
 * Mapper interface for converting between {@link Watchlist} entities, builders, and DTOs.
 */
public interface WatchlistMapper {
    /**
     * Convert a {@link Watchlist} entity to a {@link WatchlistResponseDTO}.
     *
     * @param watchlist the watchlist entity
     * @return the watchlist response DTO
     */
    WatchlistResponseDTO toDto(Watchlist watchlist);

    /**
     * Convert a {@link Watchlist} entity to a {@link WatchlistResponseDTO} with its contents.
     *
     * @param watchlist the watchlist entity
     * @param contents  the list of content response DTOs
     * @return the watchlist response DTO
     */
    WatchlistResponseDTO toDto(Watchlist watchlist, List<ContentResponseDTO> contents);

    /**
     * Convert a {@link Watchlist} entity to its builder representation.
     *
     * @param watchlist the watchlist entity
     * @return the corresponding builder
     */
    WatchlistBuilder toBuilder(Watchlist watchlist);

    /**
     * Create a {@link Watchlist} entity from the given request DTO and member.
     *
     * @param watchlistRequestDTO the watchlist request DTO
     * @param member              the member creating the watchlist
     * @return the created watchlist entity
     */
    Watchlist toEntity(WatchlistRequestDTO watchlistRequestDTO, Member member);
}
