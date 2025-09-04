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

    @Override
    public MemberBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    @Override
    public MemberBuilder lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    @Override
    public MemberBuilder birthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    @Override
    public MemberBuilder favoriteGenres(List<Genre> favoriteGenres) {
        this.favoriteGenres = favoriteGenres;
        return this;
    }

    @Override
    public Member build() {
        return new Member(this);
    }
}
