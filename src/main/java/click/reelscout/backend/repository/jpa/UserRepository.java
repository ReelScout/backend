package click.reelscout.backend.repository.jpa;

import click.reelscout.backend.model.jpa.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository <U extends User> extends JpaRepository<U, Long> {
    Optional<U> findByEmail(String email);

    Optional<U> findByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    Optional<U> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);

    boolean existsByUsernameOrEmail(String username, String email);

    boolean existsByEmailAndIdIsNot(String email, Long id);

    boolean existsByUsernameAndIdIsNot(String username, Long id);
}
