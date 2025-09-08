package click.reelscout.backend.dto.response;

import click.reelscout.backend.model.jpa.FriendshipStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class FriendshipResponseDTO {
    private Long id;
    private FriendshipStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FriendshipResponseDTO(Long id, FriendshipStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}

