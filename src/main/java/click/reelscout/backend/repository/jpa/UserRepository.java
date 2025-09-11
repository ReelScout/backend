package click.reelscout.backend.repository.jpa;

import click.reelscout.backend.model.jpa.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing User entities.
 * Provides methods for retrieving and managing users.
 *
 * @param <U> the type of user entity
 */
@Repository
public interface UserRepository <U extends User> extends JpaRepository<U, Long> {

    /**
     * Finds a user by their email address.
     *
     * @param email the email address of the user
     * @return an optional containing the user if found
     */
    Optional<U> findByEmail(String email);

    /**
     * Finds a user by their username.
     *
     * @param username the username of the user
     * @return an optional containing the user if found
     */
    Optional<U> findByUsername(String username);

    /**
     * Finds a user by their username or email address.
     *
     * @param usernameOrEmail the username or email address of the user
     * @return an optional containing the user if found
     */
    @Query("SELECT u FROM User u WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    Optional<U> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);

    /**
     * Checks if a user exists by their username or email address.
     *
     * @param username the username of the user
     * @param email the email address of the user
     * @return true if the user exists, false otherwise
     */
    boolean existsByUsernameOrEmail(String username, String email);

    /**
     * Checks if a user exists by their email address, excluding a specific user ID.
     *
     * @param email the email address of the user
     * @param id the ID of the user to exclude
     * @return true if a user with the email exists excluding the specified ID, false otherwise
     */
    boolean existsByEmailAndIdIsNot(String email, Long id);

    /**
     * Checks if a user exists by their username, excluding a specific user ID.
     *
     * @param username the username of the user
     * @param id the ID of the user to exclude
     * @return true if a user with the username exists excluding the specified ID, false otherwise
     */
    boolean existsByUsernameAndIdIsNot(String username, Long id);
}
