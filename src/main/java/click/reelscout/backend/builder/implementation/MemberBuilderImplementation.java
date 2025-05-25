package click.reelscout.backend.builder.implementation;

import click.reelscout.backend.builder.definition.MemberBuilder;
import click.reelscout.backend.model.Role;
import click.reelscout.backend.model.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Getter
public class MemberBuilderImplementation implements MemberBuilder {
    private final PasswordEncoder passwordEncoder;

    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String username;
    private String email;
    private String password;
    private Role role;

    @Override
    public MemberBuilder id(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public MemberBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    @Override
    public MemberBuilder lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    @Override
    public MemberBuilder birthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    @Override
    public MemberBuilder username(String username) {
        this.username = username;
        return this;
    }

    @Override
    public MemberBuilder email(String email) {
        this.email = email;
        return this;
    }

    @Override
    public MemberBuilder password(String password) {
        this.password = passwordEncoder.encode(password);
        return this;
    }

    @Override
    public MemberBuilder role(Role role) {
        this.role = role;
        return this;
    }

    @Override
    public Member build() {
        return new Member(this);
    }
}
