package click.reelscout.backend.model.jpa;

import click.reelscout.backend.builder.implementation.MemberBuilderImplementation;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

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

    public Member(MemberBuilderImplementation builder) {
        super(builder.getId(), builder.getUsername(), builder.getEmail(), builder.getPassword(), builder.getRole(), builder.getS3ImageKey());
        this.firstName = builder.getFirstName();
        this.lastName = builder.getLastName();
        this.birthDate = builder.getBirthDate();
    }

    @Override
    public boolean superEquals(User other) {
        return super.equals(other);
    }
}