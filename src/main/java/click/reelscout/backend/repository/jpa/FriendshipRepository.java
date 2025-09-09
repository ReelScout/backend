package click.reelscout.backend.repository.jpa;

import click.reelscout.backend.model.jpa.Friendship;
import click.reelscout.backend.model.jpa.FriendshipStatus;
import click.reelscout.backend.model.jpa.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    @Query("SELECT f FROM Friendship f WHERE (f.requester = :m1 AND f.addressee = :m2) OR (f.requester = :m2 AND f.addressee = :m1)")
    Optional<Friendship> findBetweenMembers(@Param("m1") Member m1, @Param("m2") Member m2);

    List<Friendship> findByAddresseeAndStatus(Member addressee, FriendshipStatus status);

    List<Friendship> findByRequesterAndStatus(Member requester, FriendshipStatus status);
}

