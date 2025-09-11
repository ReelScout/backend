package click.reelscout.backend.model.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * Embeddable class representing a Location with address details.
 */
@Embeddable
@Getter
@NoArgsConstructor
public class Location implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column
    private String address;

    @Column
    private String city;

    @Column
    private String state;

    @Column
    private String country;

    @Column
    private String postalCode;
}