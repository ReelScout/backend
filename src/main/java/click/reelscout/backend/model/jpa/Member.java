package click.reelscout.backend.model.jpa;

import click.reelscout.backend.builder.implementation.MemberBuilderImplementation;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

/**
 * Entity representing a Member, which is a type of User with additional attributes.
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
public class Member extends User {
    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private LocalDate birthDate;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Genre> favoriteGenres;

    public Member(MemberBuilderImplementation builder) {
        super(builder.getId(), builder.getUsername(), builder.getEmail(), builder.getPassword(), builder.getRole(), builder.getS3ImageKey(), builder.getSuspendedUntil(), builder.getSuspendedReason());
        this.firstName = builder.getFirstName();
        this.lastName = builder.getLastName();
        this.birthDate = builder.getBirthDate();
        this.favoriteGenres = builder.getFavoriteGenres();
    }

    @Override
    public boolean superEquals(User other) {
        return super.equals(other);
    }
}
