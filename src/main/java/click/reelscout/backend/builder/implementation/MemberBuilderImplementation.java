package click.reelscout.backend.builder.implementation;

import click.reelscout.backend.builder.definition.MemberBuilder;
import click.reelscout.backend.model.jpa.Genre;
import click.reelscout.backend.model.jpa.Member;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Getter
public class MemberBuilderImplementation extends AbstractUserBuilderImplementation<Member, MemberBuilder> implements MemberBuilder {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private List<Genre> favoriteGenres;

    /** {@inheritDoc} */
    @Override
    public MemberBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public MemberBuilder lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public MemberBuilder birthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public MemberBuilder favoriteGenres(List<Genre> favoriteGenres) {
        this.favoriteGenres = favoriteGenres;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public Member build() {
        return new Member(this);
    }
}
