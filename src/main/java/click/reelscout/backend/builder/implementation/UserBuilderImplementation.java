package click.reelscout.backend.builder.implementation;

import click.reelscout.backend.builder.definition.UserBuilder;
import click.reelscout.backend.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserBuilderImplementation implements UserBuilder {
    private Long id;
    private String username;
    private String email;
    private String password;

    @Override
    public UserBuilder id(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public UserBuilder username(String username) {
        this.username = username;
        return this;
    }

    @Override
    public UserBuilder email(String email) {
        this.email = email;
        return this;
    }

    @Override
    public UserBuilder password(String password) {
        this.password = password;
        return this;
    }

    @Override
    public User build() {
        return new User(id, username, email, password);
    }
}
