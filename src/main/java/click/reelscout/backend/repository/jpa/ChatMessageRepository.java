package click.reelscout.backend.repository.jpa;

import click.reelscout.backend.model.jpa.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query("SELECT m FROM ChatMessage m WHERE (LOWER(m.sender) = LOWER(:a) AND LOWER(m.recipient) = LOWER(:b)) OR (LOWER(m.sender) = LOWER(:b) AND LOWER(m.recipient) = LOWER(:a)) ORDER BY m.timestamp ASC")
    Page<ChatMessage> findDirectHistory(@Param("a") String a, @Param("b") String b, Pageable pageable);

    @Query("SELECT m FROM ChatMessage m WHERE LOWER(m.sender) = LOWER(:user) OR LOWER(m.recipient) = LOWER(:user) ORDER BY m.timestamp DESC")
    Page<ChatMessage> findRecentDmMessages(@Param("user") String user, Pageable pageable);
}
