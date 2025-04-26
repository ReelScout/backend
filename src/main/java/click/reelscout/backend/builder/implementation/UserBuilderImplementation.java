package click.reelscout.backend.builder.implementation;

import click.reelscout.backend.builder.definition.UserBuilder;
import click.reelscout.backend.model.Role;
import click.reelscout.backend.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class UserBuilderImplementation implements UserBuilder {
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String username;
    private String email;
    private String password;
    private Role role;

    @Override
    public UserBuilder id(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public UserBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    @Override
    public UserBuilder lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    @Override
    public UserBuilder birthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
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
    public UserBuilder role(Role role) {
        this.role = role;
        return this;
    }

    @Override
    public User build() {
        return new User(id, firstName, lastName, birthDate, username, email, password, role);
    }
}
