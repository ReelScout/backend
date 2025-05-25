package click.reelscout.backend.dto.response;

import click.reelscout.backend.model.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class MemberResponseDTO extends UserResponseDTO {
        private String firstName;
        private String lastName;
        private LocalDate birthDate;

        public MemberResponseDTO(
                Long id,
                String firstName,
                String lastName,
                LocalDate birthDate,
                String username,
                String email,
                Role role
        ) {
            super(id, username, email, role);
            this.firstName = firstName;
            this.lastName = lastName;
            this.birthDate = birthDate;
        }
}
