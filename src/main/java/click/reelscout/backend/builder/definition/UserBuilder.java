package click.reelscout.backend.builder.definition;

import click.reelscout.backend.model.Role;
import click.reelscout.backend.model.User;

public interface UserBuilder<U extends User, T extends UserBuilder<U, T>> extends EntityBuilder<U, T> {
    T username(String username);
    T email(String email);
    T password(String password);
    T role(Role role);
}
