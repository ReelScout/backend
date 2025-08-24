package click.reelscout.backend.builder.definition;

import click.reelscout.backend.model.Member;

import java.time.LocalDate;

public interface MemberBuilder extends UserBuilder<Member, MemberBuilder> {
    MemberBuilder firstName(String name);
    MemberBuilder lastName(String name);
    MemberBuilder birthDate(LocalDate birthDate);
}