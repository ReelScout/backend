package click.reelscout.backend.builder.definition;

import click.reelscout.backend.model.jpa.Role;
import click.reelscout.backend.model.jpa.User;

/**
 * Builder contract for creating {@link User} instances and subclasses.
 * <p>
 * Provides common fluent setters used by concrete user builders.
 */
public interface
UserBuilder<U extends User, B extends UserBuilder<U, B>> extends EntityBuilder<U, B> {
    /** Set username. */
    B username(String username);
    /** Set email. */
    B email(String email);
    /** Set password. */
    B password(String password);
    /** Set role. */
    B role(Role role);
    /** Set s3 image key. */
    B s3ImageKey(String s3ImageKey);
    /** Set suspension end time. */
    B suspendedUntil(java.time.LocalDateTime suspendedUntil);
    /** Set suspension reason. */
    B suspendedReason(String suspendedReason);
}
