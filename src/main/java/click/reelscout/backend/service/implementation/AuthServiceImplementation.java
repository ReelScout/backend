package click.reelscout.backend.service.implementation;

import click.reelscout.backend.builder.definition.UserBuilder;
import click.reelscout.backend.dto.request.MemberRequestDTO;
import click.reelscout.backend.dto.request.UserLoginRequestDTO;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.UserLoginResponseDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.exception.custom.EntityCreateException;
import click.reelscout.backend.exception.custom.EntityNotFoundException;
import click.reelscout.backend.exception.custom.InvalidCredentialsException;
import click.reelscout.backend.factory.UserMapperFactory;
import click.reelscout.backend.factory.UserMapperFactoryRegistry;
import click.reelscout.backend.mapper.definition.UserMapper;
import click.reelscout.backend.model.jpa.Genre;
import click.reelscout.backend.model.jpa.User;
import click.reelscout.backend.repository.elasticsearch.UserElasticRepository;
import click.reelscout.backend.repository.jpa.GenreRepository;
import click.reelscout.backend.repository.jpa.UserRepository;
import click.reelscout.backend.s3.S3Service;
import click.reelscout.backend.security.JwtService;
import click.reelscout.backend.service.definition.AuthService;
import click.reelscout.backend.strategy.UserMapperContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
@Service
public class AuthServiceImplementation <U extends User, B extends UserBuilder<U,B>, R extends UserRequestDTO, S extends UserResponseDTO, M extends UserMapper<U,R,S,B>> implements AuthService<R> {
    private final UserRepository<U> userRepository;
    private final UserElasticRepository userElasticRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapperContext<U,B,R,S,M> userMapperContext;
    private final UserMapperFactoryRegistry<U,B,R,S,M, UserMapperFactory<U,B,R,S,M>> userMapperFactoryRegistry;
    private final S3Service s3Service;
    private final GenreRepository genreRepository;

    @Override
    public UserLoginResponseDTO login(UserLoginRequestDTO userLoginRequestDTO) {
        return login(userLoginRequestDTO.getUsername(), userLoginRequestDTO.getPassword());
    }

    public UserLoginResponseDTO login(String username, String password) {
        U user = userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new EntityNotFoundException(User.class));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        userMapperContext.setUserMapper(userMapperFactoryRegistry.getMapperFor(user));

        String jwtToken = jwtService.generateToken(user);

        return new UserLoginResponseDTO(jwtToken);
    }

    @Override
    public UserLoginResponseDTO register(R userRequestDTO) {
        if (userRepository.existsByUsernameOrEmail(userRequestDTO.getUsername(), userRequestDTO.getEmail())) {
            throw new EntityCreateException("User already exists");
        }

        userMapperContext.setUserMapper(userMapperFactoryRegistry.getMapperFor(userRequestDTO));

        if (userRequestDTO instanceof MemberRequestDTO memberRequestDTO && memberRequestDTO.getFavoriteGenres() != null) {
            List<Genre> savedGenres = genreRepository.findAllByNameIgnoreCaseIn(
                    memberRequestDTO.getFavoriteGenres()
                            .stream()
                            .map(Genre::getName)
                            .toList()
            );
            memberRequestDTO.setFavoriteGenres(savedGenres);
        }

        try {
            String s3ImageKey = userRequestDTO.getBase64Image() != null ? "user/" + UUID.randomUUID() : null;

            U saved = userRepository.save(userMapperContext.toEntity(userRequestDTO, s3ImageKey));

            userElasticRepository.save(userMapperContext.toUserDoc(saved));

            s3Service.uploadFile(s3ImageKey, userRequestDTO.getBase64Image());
        } catch (Exception e) {
            throw new EntityCreateException(User.class);
        }

        return login(userRequestDTO.getUsername(), userRequestDTO.getPassword());
    }
}
