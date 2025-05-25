package click.reelscout.backend.service.implementation;

import click.reelscout.backend.builder.implementation.MemberBuilderImplementation;
import click.reelscout.backend.builder.implementation.ProductionCompanyBuilderImplementation;
import click.reelscout.backend.dto.request.MemberRequestDTO;
import click.reelscout.backend.dto.request.ProductionCompanyRequestDTO;
import click.reelscout.backend.dto.request.UserLoginRequestDTO;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.UserLoginResponseDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.exception.custom.EntityCreateException;
import click.reelscout.backend.exception.custom.InvalidCredentialsException;
import click.reelscout.backend.mapper.implemetation.MemberMapperImplementation;
import click.reelscout.backend.mapper.implemetation.ProductionCompanyMapperImplementation;
import click.reelscout.backend.model.User;
import click.reelscout.backend.repository.UserRepository;
import click.reelscout.backend.security.JwtService;
import click.reelscout.backend.service.definition.AuthService;
import click.reelscout.backend.strategy.UserMapperContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthServiceImplementation implements AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapperContext userMapperContext;

    @Override
    public UserLoginResponseDTO login(UserLoginRequestDTO userLoginRequestDTO) {
        return login(userLoginRequestDTO.getUsername(), userLoginRequestDTO.getPassword());
    }

    private UserLoginResponseDTO login(String username, String password) {
        User user = userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new EntityCreateException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        UserResponseDTO userResponseDTO = userMapperContext.toDto(user);
        String jwtToken = jwtService.generateToken(userResponseDTO);

        return new UserLoginResponseDTO(jwtToken);
    }

    @Override
    public UserLoginResponseDTO register(UserRequestDTO userRequestDTO) {
        if (userRepository.existsByUsernameOrEmail(userRequestDTO.getUsername(), userRequestDTO.getEmail())) {
            throw new EntityCreateException("User already exists");
        }

        switch (userRequestDTO) {
            case MemberRequestDTO memberRequestDTO -> userMapperContext.setUserMapper(new MemberMapperImplementation(new MemberBuilderImplementation(passwordEncoder)));
            case ProductionCompanyRequestDTO productionCompanyRequestDTO -> userMapperContext.setUserMapper(new ProductionCompanyMapperImplementation(new ProductionCompanyBuilderImplementation(passwordEncoder)));
            default -> throw new EntityCreateException("Invalid user type");
        }

        try {
            userRepository.save(userMapperContext.toEntity(userRequestDTO));
        } catch (Exception e) {
            throw new EntityCreateException(User.class);
        }

        return login(userRequestDTO.getUsername(), userRequestDTO.getPassword());
    }
}
