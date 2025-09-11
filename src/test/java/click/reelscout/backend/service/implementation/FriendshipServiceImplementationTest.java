package click.reelscout.backend.service.implementation;

import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.dto.response.FriendshipResponseDTO;
import click.reelscout.backend.dto.response.MemberResponseDTO;
import click.reelscout.backend.exception.custom.EntityCreateException;
import click.reelscout.backend.exception.custom.EntityDeleteException;
import click.reelscout.backend.exception.custom.EntityNotFoundException;
import click.reelscout.backend.exception.custom.EntityUpdateException;
import click.reelscout.backend.mapper.definition.FriendshipMapper;
import click.reelscout.backend.builder.definition.FriendshipBuilder;
import click.reelscout.backend.mapper.definition.MemberMapper;
import click.reelscout.backend.model.jpa.Friendship;
import click.reelscout.backend.model.jpa.FriendshipStatus;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.repository.jpa.FriendshipRepository;
import click.reelscout.backend.repository.jpa.UserRepository;
import click.reelscout.backend.s3.S3Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FriendshipServiceImplementation.
 * Focus: behavior of public methods, interactions with repository/mapper/S3, and thrown exceptions.
 */
@ExtendWith(MockitoExtension.class)
class FriendshipServiceImplementationTest {

    @Mock FriendshipRepository friendshipRepository;
    @Mock UserRepository<Member> userRepository;
    @Mock FriendshipMapper friendshipMapper;
    @Mock MemberMapper memberMapper;
    @Mock S3Service s3Service;

    @InjectMocks FriendshipServiceImplementation service;

    // ---------- helpers ----------

    /** Create a mock Member with the given ID. */
    private static Member mkMember(Long id) {
        Member m = mock(Member.class);
        lenient().when(m.getId()).thenReturn(id);
        // Optional: image key (for getFriends mapping)
        lenient().when(m.getS3ImageKey()).thenReturn("img-" + id);
        return m;
    }

    /** Create a mock Friendship with the given requester, addressee, and status. */
    private static Friendship mkFriendship(Member requester, Member addressee, FriendshipStatus status) {
        Friendship f = mock(Friendship.class);
        lenient().when(f.getRequester()).thenReturn(requester);
        lenient().when(f.getAddressee()).thenReturn(addressee);
        lenient().when(f.getStatus()).thenReturn(status);
        return f;
    }

    // =====================================================================================
    // sendRequest
    // =====================================================================================
    @Nested
    class SendRequest {

        /**
         * Tests for sendRequest():
         * - throws if addressee not found
         * - cannot send request to self
         * - errors on existing ACCEPTED/PENDING/REJECTED relationships
         * - saves PENDING friendship and returns success response
         */
        @Test
        @DisplayName("sendRequest(): throws if addressee not found")
        void addresseeNotFound() {
            // Arrange
            Member requester = mkMember(1L);
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            // Act + Assert
            assertThatThrownBy(() -> service.sendRequest(requester, 99L))
                    .isInstanceOf(EntityNotFoundException.class);

            verify(userRepository).findById(99L);
            verifyNoMoreInteractions(userRepository, friendshipRepository, friendshipMapper);
        }

        /**
         * Tests that a member cannot send a friend request to themselves.
         */
        @Test
        @DisplayName("sendRequest(): cannot send request to self")
        void cannotRequestSelf() {
            // Arrange
            Member requester = mkMember(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(requester));

            // Act + Assert
            assertThatThrownBy(() -> service.sendRequest(requester, 1L))
                    .isInstanceOf(EntityCreateException.class)
                    .hasMessageContaining("cannot send a friend request to yourself");

            verify(userRepository).findById(1L);
            verifyNoMoreInteractions(userRepository, friendshipRepository, friendshipMapper);
        }

        /**
         * Tests that existing friendships with statuses ACCEPTED, PENDING, or REJECTED
         * prevent sending a new friend request.
         */
        @Test
        @DisplayName("sendRequest(): errors on existing ACCEPTED/PENDING/REJECTED relationships")
        void existingRelationshipBlocks() {
            // Arrange
            Member requester = mkMember(1L);
            Member addressee = mkMember(2L);
            when(userRepository.findById(2L)).thenReturn(Optional.of(addressee));

            // Existing friendship with various statuses
            for (FriendshipStatus s : new FriendshipStatus[]{FriendshipStatus.ACCEPTED, FriendshipStatus.PENDING, FriendshipStatus.REJECTED}) {
                Friendship existing = mkFriendship(requester, addressee, s);
                when(friendshipRepository.findBetweenMembers(requester, addressee)).thenReturn(Optional.of(existing));

                // Act + Assert
                assertThatThrownBy(() -> service.sendRequest(requester, 2L))
                        .isInstanceOf(EntityCreateException.class);

                // reset repository/mapper for next iteration; userRepository stubbing remains valid
                reset(friendshipRepository, friendshipMapper);
            }
        }

        /**
         * Tests that a valid friend request is saved as PENDING and returns a success response.
         */
        @Test
        @DisplayName("sendRequest(): saves PENDING friendship and returns success response")
        void savesPendingAndReturnsOk() {
            // Arrange
            Member requester = mkMember(1L);
            Member addressee = mkMember(2L);
            when(userRepository.findById(2L)).thenReturn(Optional.of(addressee));
            when(friendshipRepository.findBetweenMembers(requester, addressee)).thenReturn(Optional.empty());

            Friendship toSave = mock(Friendship.class);
            when(friendshipMapper.toEntity(requester, addressee, FriendshipStatus.PENDING)).thenReturn(toSave);

            // Act
            CustomResponseDTO resp = service.sendRequest(requester, 2L);

            // Assert
            assertThat(resp.getMessage()).containsIgnoringCase("friend request sent");
            InOrder inOrder = inOrder(userRepository, friendshipRepository, friendshipMapper);
            inOrder.verify(userRepository).findById(2L);
            inOrder.verify(friendshipRepository).findBetweenMembers(requester, addressee);
            inOrder.verify(friendshipMapper).toEntity(requester, addressee, FriendshipStatus.PENDING);
            inOrder.verify(friendshipRepository).save(toSave);
            verifyNoMoreInteractions(userRepository, friendshipRepository, friendshipMapper);
        }
    }

    // =====================================================================================
    // acceptRequest
    // =====================================================================================
    @Nested
    class AcceptRequest {

        /**
         * Tests that acceptRequest throws an EntityNotFoundException
         * if the requester member is not found.
         */
        @Test
        @DisplayName("acceptRequest(): throws if requester not found")
        void requesterNotFound() {
            // Arrange
            Member addressee = mkMember(2L);
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            // Act + Assert
            assertThatThrownBy(() -> service.acceptRequest(addressee, 1L))
                    .isInstanceOf(EntityNotFoundException.class);

            verify(userRepository).findById(1L);
            verifyNoMoreInteractions(userRepository, friendshipRepository, friendshipMapper);
        }

        /**
         * Tests that acceptRequest throws an EntityNotFoundException
         * if no friendship exists between the requester and addressee.
         */
        @Test
        @DisplayName("acceptRequest(): throws if no friendship found between members")
        void friendshipNotFound() {
            // Arrange
            Member addressee = mkMember(2L);
            Member requester = mkMember(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
            when(friendshipRepository.findBetweenMembers(requester, addressee)).thenReturn(Optional.empty());

            // Act + Assert
            assertThatThrownBy(() -> service.acceptRequest(addressee, 1L))
                    .isInstanceOf(EntityNotFoundException.class);

            verify(friendshipRepository).findBetweenMembers(requester, addressee);
        }

        /**
         * Tests that acceptRequest throws an EntityUpdateException
         * if the addressee is not the current user.
         */
        @Test
        @DisplayName("acceptRequest(): throws if addressee is not the current user")
        void notAuthorizedAddressee() {
            // Arrange
            Member addressee = mkMember(2L);
            Member requester = mkMember(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(requester));

            Member someoneElse = mkMember(3L);
            Friendship friendship = mkFriendship(requester, someoneElse, FriendshipStatus.PENDING);
            when(friendshipRepository.findBetweenMembers(requester, addressee)).thenReturn(Optional.of(friendship));

            // Act + Assert
            assertThatThrownBy(() -> service.acceptRequest(addressee, 1L))
                    .isInstanceOf(EntityUpdateException.class)
                    .hasMessageContaining("not authorized");

            verify(friendshipRepository).findBetweenMembers(requester, addressee);
        }

        /**
         * Tests that acceptRequest throws an EntityUpdateException
         * if the friendship status is not PENDING.
         */
        @Test
        @DisplayName("acceptRequest(): throws if friendship is not PENDING")
        void notPending() {
            // Arrange
            Member addressee = mkMember(2L);
            Member requester = mkMember(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(requester));

            Friendship friendship = mkFriendship(requester, addressee, FriendshipStatus.ACCEPTED);
            when(friendshipRepository.findBetweenMembers(requester, addressee)).thenReturn(Optional.of(friendship));
            // addressee matches
            when(friendship.getAddressee()).thenReturn(addressee);

            // Act + Assert
            assertThatThrownBy(() -> service.acceptRequest(addressee, 1L))
                    .isInstanceOf(EntityUpdateException.class)
                    .hasMessageContaining("not pending");
        }

        /**
         * Tests that acceptRequest successfully updates a PENDING friendship to ACCEPTED
         * and returns a success response.
         */
        @Test
        @DisplayName("acceptRequest(): maps to builder -> status ACCEPTED -> save -> returns success")
        void acceptPendingOk() {
            // Arrange
            Member addressee = mkMember(2L);
            Member requester = mkMember(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(requester));

            Friendship friendship = mkFriendship(requester, addressee, FriendshipStatus.PENDING);
            when(friendshipRepository.findBetweenMembers(requester, addressee)).thenReturn(Optional.of(friendship));
            when(friendship.getAddressee()).thenReturn(addressee);

            // Mock builder chain
            FriendshipBuilder builder = mock(FriendshipBuilder.class);
            Friendship updated = mkFriendship(requester, addressee, FriendshipStatus.ACCEPTED);

            when(friendshipMapper.toBuilder(friendship)).thenReturn(builder);
            when(builder.status(FriendshipStatus.ACCEPTED)).thenReturn(builder);
            when(builder.build()).thenReturn(updated);

            // Act
            CustomResponseDTO resp = service.acceptRequest(addressee, 1L);

            // Assert
            assertThat(resp.getMessage()).containsIgnoringCase("accepted");
            verify(friendshipMapper).toBuilder(friendship);
            verify(friendshipRepository).save(updated);
        }
    }

    // =====================================================================================
    // rejectRequest
    // =====================================================================================
    @Nested
    class RejectRequest {

        /**
         * Test that rejectRequest throws an EntityNotFoundException
         * if the requester member is not found.
         */
        @Test
        @DisplayName("rejectRequest(): throws if requester not found")
        void requesterNotFound() {
            // Arrange
            Member addressee = mkMember(2L);
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            // Act + Assert
            assertThatThrownBy(() -> service.rejectRequest(addressee, 1L))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        /**
         * Tests that rejectRequest throws an EntityNotFoundException
         * if no friendship exists between the requester and addressee.
         */
        @Test
        @DisplayName("rejectRequest(): throws if friendship not found")
        void friendshipNotFound() {
            // Arrange
            Member addressee = mkMember(2L);
            Member requester = mkMember(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
            when(friendshipRepository.findBetweenMembers(requester, addressee)).thenReturn(Optional.empty());

            // Act + Assert
            assertThatThrownBy(() -> service.rejectRequest(addressee, 1L))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        /**
         * Tests that rejectRequest throws an EntityUpdateException
         * if the addressee is not the current user.
         */
        @Test
        @DisplayName("rejectRequest(): throws if not authorized")
        void notAuthorizedAddressee() {
            // Arrange
            Member addressee = mkMember(2L);
            Member requester = mkMember(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(requester));

            Member someoneElse = mkMember(3L);
            Friendship friendship = mkFriendship(requester, someoneElse, FriendshipStatus.PENDING);
            when(friendshipRepository.findBetweenMembers(requester, addressee)).thenReturn(Optional.of(friendship));

            // Act + Assert
            assertThatThrownBy(() -> service.rejectRequest(addressee, 1L))
                    .isInstanceOf(EntityUpdateException.class)
                    .hasMessageContaining("not authorized");
        }

        /**
         * Tests that rejectRequest throws an EntityUpdateException
         * if the friendship status is not PENDING.
         */
        @Test
        @DisplayName("rejectRequest(): throws if request is not PENDING")
        void notPending() {
            // Arrange
            Member addressee = mkMember(2L);
            Member requester = mkMember(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(requester));

            Friendship friendship = mkFriendship(requester, addressee, FriendshipStatus.ACCEPTED);
            when(friendshipRepository.findBetweenMembers(requester, addressee)).thenReturn(Optional.of(friendship));
            when(friendship.getAddressee()).thenReturn(addressee);

            // Act + Assert
            assertThatThrownBy(() -> service.rejectRequest(addressee, 1L))
                    .isInstanceOf(EntityUpdateException.class)
                    .hasMessageContaining("not pending");
        }

        @Test
        @DisplayName("rejectRequest(): maps to builder -> status REJECTED -> save -> returns success")
        void rejectPendingOk() {
            // Arrange
            Member addressee = mkMember(2L);
            Member requester = mkMember(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(requester));

            Friendship friendship = mkFriendship(requester, addressee, FriendshipStatus.PENDING);
            when(friendshipRepository.findBetweenMembers(requester, addressee)).thenReturn(Optional.of(friendship));
            when(friendship.getAddressee()).thenReturn(addressee);

            // Mock builder chain
            var builder = mock(click.reelscout.backend.builder.definition.FriendshipBuilder.class);
            Friendship updated = mkFriendship(requester, addressee, FriendshipStatus.REJECTED);

            when(friendshipMapper.toBuilder(friendship)).thenReturn(builder);
            when(builder.status(FriendshipStatus.REJECTED)).thenReturn(builder);
            when(builder.build()).thenReturn(updated);

            // Act
            CustomResponseDTO resp = service.rejectRequest(addressee, 1L);

            // Assert
            assertThat(resp.getMessage()).containsIgnoringCase("rejected");
            verify(friendshipMapper).toBuilder(friendship);
            verify(friendshipRepository).save(updated);
        }
    }

    // =====================================================================================
    // removeFriend
    // =====================================================================================
    @Nested
    class RemoveFriend {

        /**
         * Tests that removeFriend throws an EntityNotFoundException
         * if the other member is not found.
         */
        @Test
        @DisplayName("removeFriend(): throws if other member not found")
        void otherNotFound() {
            // Arrange
            Member me = mkMember(1L);
            when(userRepository.findById(2L)).thenReturn(Optional.empty());

            // Act + Assert
            assertThatThrownBy(() -> service.removeFriend(me, 2L))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        /**
         * Tests that removeFriend throws an EntityNotFoundException
         * if no friendship exists between the two members.
         */
        @Test
        @DisplayName("removeFriend(): throws if friendship not found")
        void friendshipNotFound() {
            // Arrange
            Member me = mkMember(1L);
            Member other = mkMember(2L);
            when(userRepository.findById(2L)).thenReturn(Optional.of(other));
            when(friendshipRepository.findBetweenMembers(me, other)).thenReturn(Optional.empty());

            // Act + Assert
            assertThatThrownBy(() -> service.removeFriend(me, 2L))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        /**
         * Tests that removeFriend throws an EntityDeleteException
         * if the friendship status is not ACCEPTED.
         */
        @Test
        @DisplayName("removeFriend(): throws if status != ACCEPTED")
        void notFriends() {
            // Arrange
            Member me = mkMember(1L);
            Member other = mkMember(2L);
            when(userRepository.findById(2L)).thenReturn(Optional.of(other));
            Friendship friendship = mkFriendship(me, other, FriendshipStatus.PENDING);
            when(friendshipRepository.findBetweenMembers(me, other)).thenReturn(Optional.of(friendship));

            // Act + Assert
            assertThatThrownBy(() -> service.removeFriend(me, 2L))
                    .isInstanceOf(EntityDeleteException.class)
                    .hasMessageContaining("not friends");
        }

        /**
         * Tests that removeFriend successfully deletes an ACCEPTED friendship
         * and returns a success response.
         */
        @Test
        @DisplayName("removeFriend(): deletes friendship and returns success")
        void removeOk() {
            // Arrange
            Member me = mkMember(1L);
            Member other = mkMember(2L);
            when(userRepository.findById(2L)).thenReturn(Optional.of(other));
            Friendship friendship = mkFriendship(me, other, FriendshipStatus.ACCEPTED);
            when(friendshipRepository.findBetweenMembers(me, other)).thenReturn(Optional.of(friendship));

            // Act
            CustomResponseDTO resp = service.removeFriend(me, 2L);

            // Assert
            assertThat(resp.getMessage()).containsIgnoringCase("removed");
            verify(friendshipRepository).delete(friendship);
        }
    }

    // =====================================================================================
    // getFriends / getIncomingRequests / getOutgoingRequests
    // =====================================================================================
    @Nested
    class QueryLists {

        /**
         * Tests that getFriends concatenates friendships where the member is
         * either the requester or addressee with status ACCEPTED,
         * and maps them to DTOs correctly.
         */
        @Test
        @DisplayName("getFriends(): concatenates ACCEPTED requester/addressee lists and maps to DTOs")
        void getFriends_concatenatesAndMaps() {
            // Arrange
            Member me = mkMember(1L);
            Member a = mkMember(2L);
            Member b = mkMember(3L);
            Friendship f1 = mkFriendship(me, a, FriendshipStatus.ACCEPTED); // me is requester
            Friendship f2 = mkFriendship(b, me, FriendshipStatus.ACCEPTED); // me is addressee

            when(friendshipRepository.findByRequesterAndStatus(me, FriendshipStatus.ACCEPTED)).thenReturn(List.of(f1));
            when(friendshipRepository.findByAddresseeAndStatus(me, FriendshipStatus.ACCEPTED)).thenReturn(List.of(f2));

            // S3 files (match S3Service return type, assume String)
            String fileMe = "file-1";
            String fileA  = "file-2";
            String fileB  = "file-3";
            when(s3Service.getFile("img-1")).thenReturn(fileMe);
            when(s3Service.getFile("img-2")).thenReturn(fileA);
            when(s3Service.getFile("img-3")).thenReturn(fileB);

            MemberResponseDTO meDto = mock(MemberResponseDTO.class);
            MemberResponseDTO aDto  = mock(MemberResponseDTO.class);
            MemberResponseDTO bDto  = mock(MemberResponseDTO.class);

            when(memberMapper.toDto(me, fileMe)).thenReturn(meDto);
            when(memberMapper.toDto(a, fileA)).thenReturn(aDto);
            when(memberMapper.toDto(b, fileB)).thenReturn(bDto);

            FriendshipResponseDTO r1 = mock(FriendshipResponseDTO.class);
            FriendshipResponseDTO r2 = mock(FriendshipResponseDTO.class);

            when(friendshipMapper.toDto(f1, meDto, aDto)).thenReturn(r1);
            when(friendshipMapper.toDto(f2, bDto, meDto)).thenReturn(r2);

            // Act
            List<FriendshipResponseDTO> result = service.getFriends(me);

            // Assert
            assertThat(result).containsExactly(r1, r2);

            // Verify transformation path for both friendships
            InOrder inOrder = inOrder(friendshipRepository, s3Service, memberMapper, friendshipMapper);
            inOrder.verify(friendshipRepository).findByRequesterAndStatus(me, FriendshipStatus.ACCEPTED);
            inOrder.verify(friendshipRepository).findByAddresseeAndStatus(me, FriendshipStatus.ACCEPTED);

            // f1 mapping
            verify(s3Service, times(2)).getFile("img-1");
            verify(s3Service).getFile("img-2");
            verify(memberMapper, times(2)).toDto(me, fileMe);
            verify(memberMapper).toDto(a, fileA);
            verify(friendshipMapper).toDto(f1, meDto, aDto);

            // f2 mapping
            verify(s3Service).getFile("img-3");
            verify(memberMapper).toDto(b, fileB);
            verify(friendshipMapper).toDto(f2, bDto, meDto);

            verifyNoMoreInteractions(friendshipRepository, s3Service, memberMapper, friendshipMapper);
        }

        /**
         * Tests that getIncomingRequests retrieves friendships where the member is
         * the addressee with status PENDING,
         * and maps them to DTOs correctly.
         */
        @Test
        @DisplayName("getIncomingRequests(): maps PENDING addressee list to DTOs")
        void getIncomingRequests_maps() {
            // Arrange
            Member me = mkMember(1L);
            Friendship f1 = mkFriendship(mkMember(2L), me, FriendshipStatus.PENDING);
            Friendship f2 = mkFriendship(mkMember(3L), me, FriendshipStatus.PENDING);
            when(friendshipRepository.findByAddresseeAndStatus(me, FriendshipStatus.PENDING))
                    .thenReturn(List.of(f1, f2));

            FriendshipResponseDTO r1 = mock(FriendshipResponseDTO.class);
            FriendshipResponseDTO r2 = mock(FriendshipResponseDTO.class);

            // These go through private toFriendshipDto -> memberMapper + s3Service + friendshipMapper.
            // For unit isolation, we can stub final result directly and verify size & identity.
            // (If you prefer, replicate the full mapping like in getFriends test.)
            when(friendshipMapper.toDto(any(), any(), any())).thenReturn(r1, r2);
            when(memberMapper.toDto(any(), any())).thenReturn(mock(MemberResponseDTO.class));
            when(s3Service.getFile(any())).thenReturn("file");

            // Act
            List<FriendshipResponseDTO> result = service.getIncomingRequests(me);

            // Assert
            assertThat(result).hasSize(2);
        }

        /**
         * Tests that getOutgoingRequests retrieves friendships where the member is
         * the requester with status PENDING,
         * and maps them to DTOs correctly.
         */
        @Test
        @DisplayName("getOutgoingRequests(): maps PENDING requester list to DTOs")
        void getOutgoingRequests_maps() {
            // Arrange
            Member me = mkMember(1L);
            Friendship f1 = mkFriendship(me, mkMember(2L), FriendshipStatus.PENDING);
            when(friendshipRepository.findByRequesterAndStatus(me, FriendshipStatus.PENDING))
                    .thenReturn(List.of(f1));

            FriendshipResponseDTO r1 = mock(FriendshipResponseDTO.class);
            when(friendshipMapper.toDto(any(), any(), any())).thenReturn(r1);
            when(memberMapper.toDto(any(), any())).thenReturn(mock(MemberResponseDTO.class));
            when(s3Service.getFile(any())).thenReturn("file");

            // Act
            List<FriendshipResponseDTO> result = service.getOutgoingRequests(me);

            // Assert
            assertThat(result).containsExactly(r1);
        }
    }
}