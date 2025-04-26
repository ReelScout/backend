package click.reelscout.backend.dto.response;

import click.reelscout.backend.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
        private Long id;
        private String firstName;
        private String lastName;
        private LocalDate birthDate;
        private String username;
        private String email;
        private Role role;
}
