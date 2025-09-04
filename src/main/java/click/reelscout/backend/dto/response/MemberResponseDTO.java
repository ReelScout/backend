package click.reelscout.backend.dto.response;

import click.reelscout.backend.model.jpa.Genre;
import click.reelscout.backend.model.jpa.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class MemberResponseDTO extends UserResponseDTO {
        private String firstName;
        private String lastName;
        private LocalDate birthDate;
        private List<Genre> favoriteGenres;

        public MemberResponseDTO(
                Long id,
                String firstName,
                String lastName,
                LocalDate birthDate,
                List<Genre> favoriteGenres,
                String username,
                String email,
                Role role,
                String base64Image
        ) {
            super(id, username, email, role, base64Image);
            this.firstName = firstName;
            this.lastName = lastName;
            this.birthDate = birthDate;
            this.favoriteGenres = favoriteGenres;
        }
}
