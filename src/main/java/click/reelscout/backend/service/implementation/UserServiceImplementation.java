package click.reelscout.backend.service.implementation;

import click.reelscout.backend.builder.definition.UserBuilder;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.exception.custom.EntityNotFoundException;
import click.reelscout.backend.exception.custom.EntityUpdateException;
import click.reelscout.backend.exception.custom.InvalidCredentialsException;
import click.reelscout.backend.factory.UserMapperFactory;
import click.reelscout.backend.factory.UserMapperFactoryRegistry;
import click.reelscout.backend.mapper.definition.UserMapper;
import click.reelscout.backend.model.User;
import click.reelscout.backend.repository.UserRepository;
import click.reelscout.backend.s3.S3Service;
import click.reelscout.backend.service.definition.UserService;
import click.reelscout.backend.strategy.UserMapperContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
@Service
public class UserServiceImplementation <U extends User, B extends UserBuilder<U, B>, R extends UserRequestDTO, S extends UserResponseDTO, M extends UserMapper<U,R,S,B>> implements UserService<U,R,S> {
    private final UserRepository<U> userRepository;
    private final UserMapperContext<U, B, R, S, UserMapper<U, R, S, B>> userMapperContext;
    private final UserMapperFactoryRegistry<U,B,R,S,M, UserMapperFactory<U,B,R,S,M>> userMapperFactoryRegistry;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;

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

    @SuppressWarnings("unchecked")
    @Override
    public U getCurrentUser() {
        U user = (U) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user == null) {
            throw new EntityNotFoundException(User.class);
        }

        return user;
    }

    @Override
    public S getCurrentUserDto() {
        U currentUser = getCurrentUser();

        userMapperContext.setUserMapper(userMapperFactoryRegistry.getMapperFor(currentUser));

        String base64Image = s3Service.getFile(currentUser.getS3ImageKey());

        return userMapperContext.toDto(currentUser, base64Image);
    }

    @Override
    public S update(R userRequestDTO) {
        U currentUser = getCurrentUser();

        if(!passwordEncoder.matches(userRequestDTO.getPassword(), currentUser.getPassword())) {
            throw new InvalidCredentialsException();
        }

        if(userRepository.existsByEmailAndIdIsNot(userRequestDTO.getEmail(), currentUser.getId())) {
            throw new EntityUpdateException("Email already in use");
        }

        if(userRepository.existsByUsernameAndIdIsNot(userRequestDTO.getUsername(), currentUser.getId())) {
            throw new EntityUpdateException("Username already in use");
        }

        userMapperContext.setUserMapper(userMapperFactoryRegistry.getMapperFor(currentUser));

        String s3ImageKey = null;

        if (userRequestDTO.getBase64Image() != null && !userRequestDTO.getBase64Image().isEmpty()) {
            if (currentUser.getS3ImageKey() == null || currentUser.getS3ImageKey().isEmpty()) {
                s3ImageKey = "user/" + UUID.randomUUID();
            } else {
                s3ImageKey = currentUser.getS3ImageKey();
            }
        }

        U user = userMapperContext.toEntity(userRequestDTO, s3ImageKey);

        U updatedUser = userMapperContext
                .toBuilder(user)
                .id(currentUser.getId())
                .role(currentUser.getRole())
                .build();

        try {
            userRepository.save(updatedUser);

            s3Service.uploadFile(s3ImageKey, userRequestDTO.getBase64Image());
        } catch (Exception e) {
            throw new EntityUpdateException(User.class);
        }

        return userMapperContext.toDto(updatedUser, userRequestDTO.getBase64Image());
    }
}
