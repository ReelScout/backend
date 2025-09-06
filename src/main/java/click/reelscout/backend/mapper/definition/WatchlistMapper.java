package click.reelscout.backend.mapper.definition;

import click.reelscout.backend.builder.definition.WatchlistBuilder;
import click.reelscout.backend.dto.request.WatchlistRequestDTO;
import click.reelscout.backend.dto.response.ContentResponseDTO;
import click.reelscout.backend.dto.response.WatchlistResponseDTO;
import click.reelscout.backend.dto.response.WatchlistWithContentsResponseDTO;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.model.jpa.Watchlist;

import java.util.List;

public interface WatchlistMapper {
    WatchlistResponseDTO toDto(Watchlist watchlist);

    WatchlistWithContentsResponseDTO toDto(Watchlist watchlist, List<ContentResponseDTO> contents);

    WatchlistBuilder toBuilder(Watchlist watchlist);

    Watchlist toEntity(WatchlistRequestDTO watchlistRequestDTO, Member member);
}
