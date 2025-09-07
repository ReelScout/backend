package click.reelscout.backend.mapper.implemetation;

import click.reelscout.backend.builder.definition.WatchlistBuilder;
import click.reelscout.backend.dto.request.WatchlistRequestDTO;
import click.reelscout.backend.dto.response.ContentResponseDTO;
import click.reelscout.backend.dto.response.WatchlistResponseDTO;
import click.reelscout.backend.dto.response.WatchlistWithContentsResponseDTO;
import click.reelscout.backend.mapper.definition.WatchlistMapper;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.model.jpa.Watchlist;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WatchlistMapperImplementation implements WatchlistMapper {
    private final WatchlistBuilder watchlistBuilder;

    @Override
    public WatchlistResponseDTO toDto(Watchlist watchlist) {
        return new WatchlistResponseDTO(
                watchlist.getId(),
                watchlist.getName(),
                watchlist.getIsPublic()
        );
    }

    @Override
    public WatchlistResponseDTO toDto(Watchlist watchlist, List<ContentResponseDTO> contents) {
        return new WatchlistWithContentsResponseDTO(
                watchlist.getId(),
                watchlist.getName(),
                watchlist.getIsPublic(),
                contents
        );
    }

    @Override
    public WatchlistBuilder toBuilder(Watchlist watchlist) {
        return watchlistBuilder
                .id(watchlist.getId())
                .name(watchlist.getName())
                .contents(watchlist.getContents())
                .isPublic(watchlist.getIsPublic())
                .member(watchlist.getMember());
    }

    @Override
    public Watchlist toEntity(WatchlistRequestDTO watchlistRequestDTO, Member member) {
        return watchlistBuilder
                .id(null)
                .name(watchlistRequestDTO.getName())
                .isPublic(watchlistRequestDTO.getIsPublic())
                .member(member)
                .build();
    }
}
