package click.reelscout.backend.service.implementation;

import click.reelscout.backend.builder.definition.UserBuilder;
import click.reelscout.backend.dto.request.UserLoginRequestDTO;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.UserLoginResponseDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.exception.custom.EntityCreateException;
import click.reelscout.backend.exception.custom.InvalidCredentialsException;
import click.reelscout.backend.factory.UserMapperFactory;
import click.reelscout.backend.factory.UserMapperFactoryRegistry;
import click.reelscout.backend.mapper.definition.UserMapper;
import click.reelscout.backend.model.User;
import click.reelscout.backend.repository.UserRepository;
import click.reelscout.backend.security.JwtService;
import click.reelscout.backend.service.definition.AuthService;
import click.reelscout.backend.strategy.UserMapperContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AuthServiceImplementation <U extends User, B extends UserBuilder<U,B>, R extends UserRequestDTO, S extends UserResponseDTO, M extends UserMapper<U,R,S,B>> implements AuthService<R> {
    private final UserRepository<U> userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapperContext<U,B,R,S,M> userMapperContext;
    private final UserMapperFactoryRegistry<U,B,R,S,M, UserMapperFactory<U,B,R,S,M>> userMapperFactoryRegistry;

    @Override
    public UserLoginResponseDTO login(UserLoginRequestDTO userLoginRequestDTO) {
        return login(userLoginRequestDTO.getUsername(), userLoginRequestDTO.getPassword());
    }

    private UserLoginResponseDTO login(String username, String password) {
        U user = userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new EntityCreateException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        userMapperContext.setUserMapper(userMapperFactoryRegistry.getMapperFor(user));

        UserResponseDTO userResponseDTO = userMapperContext.toDto(user, null);
        String jwtToken = jwtService.generateToken(userResponseDTO);

        return new UserLoginResponseDTO(jwtToken);
    }

    @Override
    public UserLoginResponseDTO register(R userRequestDTO) {
        if (userRepository.existsByUsernameOrEmail(userRequestDTO.getUsername(), userRequestDTO.getEmail())) {
            throw new EntityCreateException("User already exists");
        }

        userMapperContext.setUserMapper(userMapperFactoryRegistry.getMapperFor(userRequestDTO));

        try {
            userRepository.save(userMapperContext.toEntity(userRequestDTO, userRequestDTO.getBase64Image()!= null ? "user/"+UUID.randomUUID() : null));
        } catch (Exception e) {
            throw new EntityCreateException(User.class);
        }

        return login(userRequestDTO.getUsername(), userRequestDTO.getPassword());
    }
}
