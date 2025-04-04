package click.reelscout.backend.builder.definition;

import click.reelscout.backend.model.User;

public interface UserBuilder {
    UserBuilder id(Long id);
    UserBuilder username(String username);
    UserBuilder email(String email);
    UserBuilder password(String password);
    User build();
}
