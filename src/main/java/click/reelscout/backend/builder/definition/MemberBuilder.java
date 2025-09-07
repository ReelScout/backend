package click.reelscout.backend.builder.definition;

import click.reelscout.backend.model.jpa.Genre;
import click.reelscout.backend.model.jpa.Member;

import java.time.LocalDate;
import java.util.List;

public interface MemberBuilder extends UserBuilder<Member, MemberBuilder> {
    MemberBuilder firstName(String name);
    MemberBuilder lastName(String name);
    MemberBuilder birthDate(LocalDate birthDate);
    MemberBuilder favoriteGenres(List<Genre> favoriteGenres);
}