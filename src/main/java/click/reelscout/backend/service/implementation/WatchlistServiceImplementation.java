package click.reelscout.backend.service.implementation;

import click.reelscout.backend.dto.request.WatchlistRequestDTO;
import click.reelscout.backend.dto.response.ContentResponseDTO;
import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.dto.response.WatchlistResponseDTO;
import click.reelscout.backend.exception.custom.EntityCreateException;
import click.reelscout.backend.exception.custom.EntityDeleteException;
import click.reelscout.backend.exception.custom.EntityNotFoundException;
import click.reelscout.backend.exception.custom.EntityUpdateException;
import click.reelscout.backend.mapper.definition.ContentMapper;
import click.reelscout.backend.mapper.definition.WatchlistMapper;
import click.reelscout.backend.model.jpa.Content;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.model.jpa.Watchlist;
import click.reelscout.backend.repository.jpa.ContentRepository;
import click.reelscout.backend.repository.jpa.WatchlistRepository;
import click.reelscout.backend.s3.S3Service;
import click.reelscout.backend.service.definition.WatchlistService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
@Service
public class WatchlistServiceImplementation implements WatchlistService {
    private final WatchlistRepository watchlistRepository;
    private final WatchlistMapper watchlistMapper;
    private final ContentRepository contentRepository;
    private final ContentMapper contentMapper;
    private final S3Service s3Service;

    @Override
    public WatchlistResponseDTO create(Member member, WatchlistRequestDTO watchlistRequestDTO) {
        try {
            Watchlist watchlist = watchlistMapper.toEntity(watchlistRequestDTO, member);

            watchlistRepository.save(watchlist);

            return watchlistMapper.toDto(watchlist);
        } catch (Exception e) {
            throw new EntityCreateException(Watchlist.class);
        }
    }

    @Override
    public WatchlistResponseDTO update(Member member, Long id, WatchlistRequestDTO watchlistRequestDTO) {
        Watchlist watchlistToUpdate = watchlistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Watchlist.class));

        if (!watchlistToUpdate.getMember().getId().equals(member.getId())) {
            throw new EntityUpdateException("You are not authorized to update this watchlist");
        }

        Watchlist updatedWatchlist = watchlistMapper.toBuilder(watchlistMapper.toEntity(watchlistRequestDTO, member))
                .id(id)
                .build();

        try {
            watchlistRepository.save(updatedWatchlist);

            return watchlistMapper.toDto(updatedWatchlist);
        } catch (Exception e) {
            throw new EntityUpdateException(Watchlist.class);
        }
    }

    @Override
    public CustomResponseDTO delete(Member member, Long id) {
        Watchlist watchlist = watchlistRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Watchlist.class));

        if (!watchlist.getMember().getId().equals(member.getId())) {
            throw new EntityDeleteException(("You are not authorized to delete this watchlist"));
        }

        try {
            watchlistRepository.delete(watchlist);

            return new CustomResponseDTO("Watchlist deleted successfully");
        } catch (Exception e) {
            throw new EntityDeleteException(Watchlist.class);
        }
    }

    @Override
    public List<WatchlistResponseDTO> getAllByMember(Member member) {
        return watchlistRepository.findAllByMember(member)
                .stream()
                .map(watchlistMapper::toDto)
                .toList();
    }

    @Override
    public WatchlistResponseDTO addContentToWatchlist(Member member, Long watchlistId, Long contentId) {
        Watchlist watchlist = watchlistRepository.findById(watchlistId).orElseThrow(() -> new EntityNotFoundException(Watchlist.class));

        Content contentToAdd = contentRepository.findById(contentId).orElseThrow(() -> new EntityNotFoundException(Content.class));

        watchlist.getContents().add(contentToAdd);

        try {
            watchlistRepository.save(watchlist);

            List<ContentResponseDTO> contents = Optional.ofNullable(watchlist.getContents())
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(content -> contentMapper.toDto(content, s3Service.getFile(content.getS3ImageKey()))).toList();

            return watchlistMapper.toDto(watchlist, contents);
        } catch (Exception e) {
            throw new EntityUpdateException("Failed to add content to " + watchlist.getName() + " watchlist");
        }
    }

    @Override
    public WatchlistResponseDTO removeContentFromWatchlist(Member member, Long watchlistId, Long contentId) {
        Watchlist watchlist = watchlistRepository.findById(watchlistId).orElseThrow(() -> new EntityNotFoundException(Watchlist.class));

        Content contentToRemove = contentRepository.findById(contentId).orElseThrow(() -> new EntityNotFoundException(Content.class));

        watchlist.getContents().remove(contentToRemove);

        try {
            watchlistRepository.save(watchlist);

            List<ContentResponseDTO> contents = Optional.ofNullable(watchlist.getContents())
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(content -> contentMapper.toDto(content, s3Service.getFile(content.getS3ImageKey()))).toList();

            return watchlistMapper.toDto(watchlist, contents);
        } catch (Exception e) {
            throw new EntityUpdateException(Watchlist.class);
        }
    }

    @Override
    public WatchlistResponseDTO getById(Member member, Long id) {
        Watchlist watchlist = watchlistRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Watchlist.class));

        if (!watchlist.getMember().getId().equals(member.getId()) && !watchlist.getIsPublic()) {
            throw new EntityNotFoundException(Watchlist.class);
        }

        List<ContentResponseDTO> contents = Optional.ofNullable(watchlist.getContents())
                .orElse(Collections.emptyList())
                .stream()
                .map(content -> contentMapper.toDto(content, s3Service.getFile(content.getS3ImageKey()))).toList();

        return watchlistMapper.toDto(watchlist, contents);
    }

    @Override
    public List<WatchlistResponseDTO> getAllByMemberAndContent(Member member, Long contentId) {
        Content content = contentRepository.findById(contentId).orElseThrow(() -> new EntityNotFoundException(Content.class));

        return Optional.ofNullable(watchlistRepository.findAllByMemberAndContentsIsContaining(member, content))
                .orElse(Collections.emptyList())
                .stream()
                .map(watchlistMapper::toDto)
                .toList();
        }
}
