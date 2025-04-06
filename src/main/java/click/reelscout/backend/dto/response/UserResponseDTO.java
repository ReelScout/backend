package click.reelscout.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class UserResponseDTO {
        private Long id;
        private String firstName;
        private String lastName;
        private LocalDate birthDate;
        private String username;
        private String email;
}
