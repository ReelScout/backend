package click.reelscout.backend.dto.response;

import click.reelscout.backend.model.jpa.FriendshipStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * DTO representing a friendship along with its associated users.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FriendshipWithUsersResponseDTO extends FriendshipResponseDTO {
    private UserResponseDTO requester;
    private UserResponseDTO addressee;

    public FriendshipWithUsersResponseDTO(Long id, FriendshipStatus status, LocalDateTime createdAt, LocalDateTime updatedAt, UserResponseDTO requester, UserResponseDTO addressee) {
        super(id, status, createdAt, updatedAt);
        this.requester = requester;
        this.addressee = addressee;
    }

}

