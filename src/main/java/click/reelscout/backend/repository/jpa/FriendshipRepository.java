package click.reelscout.backend.repository.jpa;

import click.reelscout.backend.model.jpa.Friendship;
import click.reelscout.backend.model.jpa.FriendshipStatus;
import click.reelscout.backend.model.jpa.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Friendship entities.
 */
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    /**
     * Finds a friendship between two members, regardless of who is the requester or addressee.
     *
     * @param m1 the first member
     * @param m2 the second member
     * @return an Optional containing the Friendship if found, or empty if not found
     */
    @Query("SELECT f FROM Friendship f WHERE (f.requester = :m1 AND f.addressee = :m2) OR (f.requester = :m2 AND f.addressee = :m1)")
    Optional<Friendship> findBetweenMembers(@Param("m1") Member m1, @Param("m2") Member m2);

    /**
     * Finds all friendships where the specified member is either the requester or the addressee.
     *
     * @param addressee the member to search for
     * @return a list of Friendships involving the specified member
     */
    List<Friendship> findByAddresseeAndStatus(Member addressee, FriendshipStatus status);

    /**
     * Finds all friendships where the specified member is the requester and the friendship has the specified status.
     *
     * @param requester the member who sent the friend request
     * @param status    the status of the friendship
     * @return a list of Friendships where the member is the requester with the specified status
     */
    List<Friendship> findByRequesterAndStatus(Member requester, FriendshipStatus status);
}

