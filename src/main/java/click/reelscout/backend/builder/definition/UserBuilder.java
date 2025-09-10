package click.reelscout.backend.builder.definition;

import click.reelscout.backend.model.jpa.Role;
import click.reelscout.backend.model.jpa.User;

public interface
UserBuilder<U extends User, B extends UserBuilder<U, B>> extends EntityBuilder<U, B> {
    B username(String username);
    B email(String email);
    B password(String password);
    B role(Role role);
    B s3ImageKey(String s3ImageKey);
    B suspendedUntil(java.time.LocalDateTime suspendedUntil);
    B suspendedReason(String suspendedReason);
}
