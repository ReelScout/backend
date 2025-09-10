package click.reelscout.backend.builder.implementation;

import click.reelscout.backend.builder.definition.UserBuilder;
import click.reelscout.backend.model.jpa.Role;
import click.reelscout.backend.model.jpa.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor
@Getter
public abstract class AbstractUserBuilderImplementation<U extends User, B extends UserBuilder<U, B>> implements UserBuilder<U, B> {
    protected Long id;
    protected String username;
    protected String email;
    protected String password;
    protected Role role;
    protected String s3ImageKey;
    protected LocalDateTime suspendedUntil;

    @Override
    public B id(Long id) {
        this.id = id;
        return (B) this;
    }

    @Override
    public B username(String username) {
        this.username = username;
        return (B) this;
    }

    @Override
    public B email(String email) {
        this.email = email;
        return (B) this;
    }

    @Override
    public B password(String password) {
        this.password = password;
        return (B) this;
    }

    @Override
    public B role(Role role) {
        this.role = role;
        return (B) this;
    }

    @Override
    public B s3ImageKey(String s3ImageKey) {
        this.s3ImageKey = s3ImageKey;
        return (B) this;
    }

    public B suspendedUntil(LocalDateTime suspendedUntil) {
        this.suspendedUntil = suspendedUntil;
        return (B) this;
    }
}
