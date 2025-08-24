package click.reelscout.backend.builder.definition;

import click.reelscout.backend.model.Role;
import click.reelscout.backend.model.User;

public interface UserBuilder<U extends User, B extends UserBuilder<U, B>> extends EntityBuilder<U, B> {
    B username(String username);
    B email(String email);
    B password(String password);
    B role(Role role);
    B s3ImageKey(String s3ImageKey);
}
