package click.reelscout.backend.service.definition;

import click.reelscout.backend.model.User;

import java.util.Optional;

public interface UserService {
    User create(User user);

    Optional<User> getByEmail(String email);

    Optional<User> getByUsername(String username);
}
