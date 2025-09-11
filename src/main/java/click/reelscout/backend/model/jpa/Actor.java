package click.reelscout.backend.model.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * Embeddable class representing an Actor with first and last names.
 */
@NoArgsConstructor
@Embeddable
@Getter
public class Actor implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;
}
