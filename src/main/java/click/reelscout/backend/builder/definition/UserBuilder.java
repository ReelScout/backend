package click.reelscout.backend.builder.definition;

import click.reelscout.backend.model.User;

import java.time.LocalDate;

public interface UserBuilder {
    UserBuilder id(Long id);
    UserBuilder firstName(String firstName);
    UserBuilder lastName(String lastName);
    UserBuilder birthDate(LocalDate birthDate);
    UserBuilder username(String username);
    UserBuilder email(String email);
    UserBuilder password(String password);
    User build();
}
