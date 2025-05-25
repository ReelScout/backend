package click.reelscout.backend.dto.response;

import click.reelscout.backend.model.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class UserResponseDTO extends EntityResponseDTO {
    private String username;
    private String email;
    private Role role;

    public UserResponseDTO(
            Long id,
            String username,
            String email,
            Role role
    ) {
        super(id);
        this.username = username;
        this.email = email;
        this.role = role;
    }
}
