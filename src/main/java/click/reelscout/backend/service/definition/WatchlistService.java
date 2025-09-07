package click.reelscout.backend.service.definition;

import click.reelscout.backend.dto.request.WatchlistRequestDTO;
import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.dto.response.WatchlistResponseDTO;
import click.reelscout.backend.model.jpa.Member;

import java.util.List;

public interface WatchlistService {
    WatchlistResponseDTO create(Member member, WatchlistRequestDTO watchlistRequestDTO);
    
    WatchlistResponseDTO update(Member member, Long id, WatchlistRequestDTO watchlistRequestDTO);

    CustomResponseDTO delete(Member member, Long id);

    List<WatchlistResponseDTO> getAllByMember(Member member);

    WatchlistResponseDTO addContentToWatchlist(Member member, Long watchlistId, Long contentId);

    WatchlistResponseDTO removeContentFromWatchlist(Member member, Long watchlistId, Long contentId);

    WatchlistResponseDTO getById(Member member, Long id);

    List<WatchlistResponseDTO> getAllByMemberAndContent(Member member, Long contentId);

    List<WatchlistResponseDTO> getAllPublicByMember(Long memberId);
}
