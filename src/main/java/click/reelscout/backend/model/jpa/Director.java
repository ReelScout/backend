package click.reelscout.backend.model.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@Getter
public class Director implements Serializable {
    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;
}
