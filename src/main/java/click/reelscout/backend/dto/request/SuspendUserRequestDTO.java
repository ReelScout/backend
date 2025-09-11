package click.reelscout.backend.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for suspending a user.
 */
@Data
public class SuspendUserRequestDTO {
    @NotNull(message = "Suspension end time is required")
    @Future(message = "Suspension end time must be in the future")
    private LocalDateTime until;

    private String reason;
}

