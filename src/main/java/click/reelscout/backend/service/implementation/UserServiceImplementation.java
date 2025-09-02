package click.reelscout.backend.service.implementation;

import click.reelscout.backend.builder.definition.UserBuilder;
import click.reelscout.backend.dto.request.UserPasswordChangeRequestDTO;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.dto.response.UserLoginResponseDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.exception.custom.EntityNotFoundException;
import click.reelscout.backend.exception.custom.EntityUpdateException;
import click.reelscout.backend.factory.UserMapperFactory;
import click.reelscout.backend.factory.UserMapperFactoryRegistry;
import click.reelscout.backend.mapper.definition.UserMapper;
import click.reelscout.backend.model.jpa.User;
import click.reelscout.backend.repository.elasticsearch.UserElasticRepository;
import click.reelscout.backend.repository.jpa.UserRepository;
import click.reelscout.backend.s3.S3Service;
import click.reelscout.backend.service.definition.AuthService;
import click.reelscout.backend.service.definition.UserService;
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
public class UserServiceImplementation <U extends User, B extends UserBuilder<U, B>, R extends UserRequestDTO, S extends UserResponseDTO, M extends UserMapper<U,R,S,B>> implements UserService<U,R,S> {
    private final UserRepository<U> userRepository;
    private final UserElasticRepository userElasticRepository;
    private final UserMapperContext<U, B, R, S, UserMapper<U, R, S, B>> userMapperContext;
    private final UserMapperFactoryRegistry<U,B,R,S,M, UserMapperFactory<U,B,R,S,M>> userMapperFactoryRegistry;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;
    private final AuthService<R> authService;

    @Override
    public List<S> getAll() {
        List<U> users = userRepository.findAll();

        return users.stream().map(user -> {
            userMapperContext.setUserMapper(userMapperFactoryRegistry.getMapperFor(user));

            String base64Image = s3Service.getFile(user.getS3ImageKey());

            return userMapperContext.toDto(user, base64Image);
        }).toList();
    }

    @Override
    public S getById(Long id) {
        U user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class));

        userMapperContext.setUserMapper(userMapperFactoryRegistry.getMapperFor(user));

        String base64Image = s3Service.getFile(user.getS3ImageKey());

        return userMapperContext.toDto(user, base64Image);
    }

    @Override
    public S getByEmail(String email) {
        U user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(User.class));

        userMapperContext.setUserMapper(userMapperFactoryRegistry.getMapperFor(user));

        String base64Image = s3Service.getFile(user.getS3ImageKey());

        return userMapperContext.toDto(user, base64Image);
    }

    @Override
    public S getByUsername(String username) {
        U user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(User.class));

        userMapperContext.setUserMapper(userMapperFactoryRegistry.getMapperFor(user));

        String base64Image = s3Service.getFile(user.getS3ImageKey());

        return userMapperContext.toDto(user, base64Image);
    }

    @Override
    public S getByUsernameOrEmail(String usernameOrEmail) {
        U user = userRepository.findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> new EntityNotFoundException(User.class));

        userMapperContext.setUserMapper(userMapperFactoryRegistry.getMapperFor(user));

        String base64Image = s3Service.getFile(user.getS3ImageKey());

        return userMapperContext.toDto(user, base64Image);
    }

    @Override
    public S getCurrentUserDto(U authenticatedUser) {
        userMapperContext.setUserMapper(userMapperFactoryRegistry.getMapperFor(authenticatedUser));

        String base64Image = s3Service.getFile(authenticatedUser.getS3ImageKey());

        return userMapperContext.toDto(authenticatedUser, base64Image);
    }

    @Override
    public UserLoginResponseDTO update(U authenticatedUser, R userRequestDTO) {
        if(!passwordEncoder.matches(userRequestDTO.getPassword(), authenticatedUser.getPassword())) {
            throw new EntityUpdateException("Password is incorrect");
        }

        if(userRepository.existsByEmailAndIdIsNot(userRequestDTO.getEmail(), authenticatedUser.getId())) {
            throw new EntityUpdateException("Email already in use");
        }

        if(userRepository.existsByUsernameAndIdIsNot(userRequestDTO.getUsername(), authenticatedUser.getId())) {
            throw new EntityUpdateException("Username already in use");
        }

        userMapperContext.setUserMapper(userMapperFactoryRegistry.getMapperFor(authenticatedUser));

        String s3ImageKey = null;

        if (userRequestDTO.getBase64Image() != null && !userRequestDTO.getBase64Image().isEmpty()) {
            if (authenticatedUser.getS3ImageKey() == null || authenticatedUser.getS3ImageKey().isEmpty()) {
                s3ImageKey = "user/" + UUID.randomUUID();
            } else {
                s3ImageKey = authenticatedUser.getS3ImageKey();
            }
        }

        U updatedUser = userMapperContext
                .toBuilder(userMapperContext.toEntity(userRequestDTO, s3ImageKey))
                .id(authenticatedUser.getId())
                .role(authenticatedUser.getRole())
                .build();

        try {
            U saved = userRepository.save(updatedUser);

            userElasticRepository.save(userMapperContext.toUserDoc(saved));

            s3Service.uploadFile(s3ImageKey, userRequestDTO.getBase64Image());
        } catch (Exception e) {
            throw new EntityUpdateException(User.class);
        }

        return authenticatedUser.superEquals(updatedUser) ? null : authService.login(updatedUser.getUsername(), userRequestDTO.getPassword());
    }

    @Override
    public CustomResponseDTO changePassword(U authenticatedUser, UserPasswordChangeRequestDTO userPasswordChangeRequestDTO) {
        if (!passwordEncoder.matches(userPasswordChangeRequestDTO.getCurrentPassword(), authenticatedUser.getPassword())) {
            throw new EntityUpdateException("Current password is incorrect");
        }

        if (!userPasswordChangeRequestDTO.getNewPassword().equals(userPasswordChangeRequestDTO.getConfirmPassword())) {
            throw new EntityUpdateException("New password and confirm password do not match");
        }

        U updatedUser = userMapperContext
                .toBuilder(authenticatedUser)
                .password(passwordEncoder.encode(userPasswordChangeRequestDTO.getNewPassword()))
                .build();

        try {
            userRepository.save(updatedUser);
        } catch (Exception e) {
            throw new EntityUpdateException("Failed to change the password");
        }

        return new CustomResponseDTO("Password changed successfully");
    }
}
