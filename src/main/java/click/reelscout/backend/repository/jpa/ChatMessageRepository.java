package click.reelscout.backend.repository.jpa;

import click.reelscout.backend.model.jpa.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository interface for managing ChatMessage entities.
 * Provides methods for retrieving chat histories and recent direct messages.
 */
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * Retrieves the chat history between two users, sorted by timestamp in ascending order.
     *
     * @param a the first user's identifier
     * @param b the second user's identifier
     * @param pageable pagination information
     * @return a page of chat messages between the two users
     */
    @Query("SELECT m FROM ChatMessage m WHERE (LOWER(m.sender) = LOWER(:a) AND LOWER(m.recipient) = LOWER(:b)) OR (LOWER(m.sender) = LOWER(:b) AND LOWER(m.recipient) = LOWER(:a)) ORDER BY m.timestamp ASC")
    Page<ChatMessage> findDirectHistory(@Param("a") String a, @Param("b") String b, Pageable pageable);

    /**
     * Fetches recent direct messages for a specific user, sorted by timestamp in descending order.
     *
     * @param user the user's identifier
     * @param pageable pagination information
     * @return a page of recent direct messages for the user
     */
    @Query("SELECT m FROM ChatMessage m WHERE LOWER(m.sender) = LOWER(:user) OR LOWER(m.recipient) = LOWER(:user) ORDER BY m.timestamp DESC")
    Page<ChatMessage> findRecentDmMessages(@Param("user") String user, Pageable pageable);
}
