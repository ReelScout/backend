package click.reelscout.backend.dto.request;

import click.reelscout.backend.model.jpa.Genre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO for member requests, extending UserRequestDTO.
 * Includes additional fields specific to members.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MemberRequestDTO extends UserRequestDTO {
        @NotBlank(message = "Firstname is mandatory")
        private String firstName;

        @NotBlank(message = "Lastname is mandatory")
        private String lastName;

        @NotNull(message = "Birthdate is mandatory")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate birthDate;

        private List<Genre> favoriteGenres;
}
