package click.reelscout.backend.service.implementation;

import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.dto.response.FriendshipResponseDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.exception.custom.EntityCreateException;
import click.reelscout.backend.exception.custom.EntityDeleteException;
import click.reelscout.backend.exception.custom.EntityNotFoundException;
import click.reelscout.backend.exception.custom.EntityUpdateException;
import click.reelscout.backend.mapper.definition.FriendshipMapper;
import click.reelscout.backend.mapper.definition.MemberMapper;
import click.reelscout.backend.model.jpa.*;
import click.reelscout.backend.repository.jpa.FriendshipRepository;
import click.reelscout.backend.repository.jpa.UserRepository;
import click.reelscout.backend.s3.S3Service;
import click.reelscout.backend.service.definition.FriendshipService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
@Service
public class FriendshipServiceImplementation implements FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final UserRepository<Member> userRepository;

    private final FriendshipMapper friendshipMapper;
    private final MemberMapper memberMapper;

    private final S3Service s3Service;

    /** {@inheritDoc} */
    @Override
    public CustomResponseDTO sendRequest(Member requester, Long addresseeId) {
        Member addressee = userRepository.findById(addresseeId)
                .orElseThrow(() -> new EntityNotFoundException(Member.class));

        if (requester.getId().equals(addressee.getId())) {
            throw new EntityCreateException("You cannot send a friend request to yourself");
        }

        Optional<Friendship> existing = friendshipRepository.findBetweenMembers(requester, addressee);
        if (existing.isPresent()) {
            Friendship f = existing.get();
            if (f.getStatus() == FriendshipStatus.ACCEPTED) {
                throw new EntityCreateException("You are already friends");
            }
            if (f.getStatus() == FriendshipStatus.PENDING) {
                throw new EntityCreateException("A friend request already exists");
            }
            if (f.getStatus() == FriendshipStatus.REJECTED) {
                throw new EntityCreateException("A previous request was rejected");
            }
        }

        try {
            friendshipRepository.save(friendshipMapper.toEntity(requester, addressee, FriendshipStatus.PENDING));
            return new CustomResponseDTO("Friend request sent");
        } catch (Exception e) {
            throw new EntityCreateException(Friendship.class);
        }
    }

    /** {@inheritDoc} */
    @Override
    public CustomResponseDTO acceptRequest(Member addressee, Long requesterId) {
        Member requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new EntityNotFoundException(Member.class));

        Friendship friendship = friendshipRepository.findBetweenMembers(requester, addressee)
                .orElseThrow(() -> new EntityNotFoundException(Friendship.class));

        if (!friendship.getAddressee().getId().equals(addressee.getId())) {
            throw new EntityUpdateException("You are not authorized to accept this request");
        }

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new EntityUpdateException("Friend request is not pending");
        }

        try {

            System.out.println(friendship);
            friendship = friendshipMapper.toBuilder(friendship)
                    .status(FriendshipStatus.ACCEPTED)
                    .build();
            System.out.println("OKKK");
            System.out.println(friendship);

            friendshipRepository.save(friendship);
            return new CustomResponseDTO("Friend request accepted");
        } catch (Exception e) {
            throw new EntityUpdateException(Friendship.class);
        }
    }

    /** {@inheritDoc} */
    @Override
    public CustomResponseDTO rejectRequest(Member addressee, Long requesterId) {
        Member requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new EntityNotFoundException(Member.class));

        Friendship friendship = friendshipRepository.findBetweenMembers(requester, addressee)
                .orElseThrow(() -> new EntityNotFoundException(Friendship.class));

        if (!friendship.getAddressee().getId().equals(addressee.getId())) {
            throw new EntityUpdateException("You are not authorized to reject this request");
        }

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new EntityUpdateException("Friend request is not pending");
        }

        try {
            friendship = friendshipMapper.toBuilder(friendship)
                    .status(FriendshipStatus.REJECTED)
                    .build();

            friendshipRepository.save(friendship);
            return new CustomResponseDTO("Friend request rejected");
        } catch (Exception e) {
            throw new EntityUpdateException(Friendship.class);
        }
    }

    /** {@inheritDoc} */
    @Override
    public CustomResponseDTO removeFriend(Member member, Long friendId) {
        Member other = userRepository.findById(friendId)
                .orElseThrow(() -> new EntityNotFoundException(Member.class));

        Friendship friendship = friendshipRepository.findBetweenMembers(member, other)
                .orElseThrow(() -> new EntityNotFoundException(Friendship.class));

        if (friendship.getStatus() != FriendshipStatus.ACCEPTED) {
            throw new EntityDeleteException("You are not friends");
        }

        try {
            friendshipRepository.delete(friendship);
            return new CustomResponseDTO("Friend removed");
        } catch (Exception e) {
            throw new EntityDeleteException(Friendship.class);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<FriendshipResponseDTO> getFriends(Member member) {
        // accepted friendships: build friendship DTOs with both users populated
        List<Friendship> acceptedAsRequester = friendshipRepository.findByRequesterAndStatus(member, FriendshipStatus.ACCEPTED);
        List<Friendship> acceptedAsAddressee = friendshipRepository.findByAddresseeAndStatus(member, FriendshipStatus.ACCEPTED);
        return java.util.stream.Stream.concat(acceptedAsRequester.stream(), acceptedAsAddressee.stream()).map(this::toFriendshipDto).toList();
    }

    /** {@inheritDoc} */
    @Override
    public List<FriendshipResponseDTO> getIncomingRequests(Member member) {
        return friendshipRepository.findByAddresseeAndStatus(member, FriendshipStatus.PENDING)
                .stream().map(this::toFriendshipDto).toList();
    }

    /** {@inheritDoc} */
    @Override
    public List<FriendshipResponseDTO> getOutgoingRequests(Member member) {
        return friendshipRepository.findByRequesterAndStatus(member, FriendshipStatus.PENDING)
                .stream().map(this::toFriendshipDto).toList();
    }

    private FriendshipResponseDTO toFriendshipDto(Friendship friendship) {
        // Build requester DTO
        UserResponseDTO requesterDto = memberMapper.toDto(friendship.getRequester(), s3Service.getFile(friendship.getRequester().getS3ImageKey()));
        UserResponseDTO addresseeDto = memberMapper.toDto(friendship.getAddressee(), s3Service.getFile(friendship.getAddressee().getS3ImageKey()));

        return friendshipMapper.toDto(friendship, requesterDto, addresseeDto);
    }
}
