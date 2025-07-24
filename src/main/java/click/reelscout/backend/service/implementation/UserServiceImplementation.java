package click.reelscout.backend.service.implementation;

import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.exception.custom.EntityNotFoundException;
import click.reelscout.backend.exception.custom.EntityUpdateException;
import click.reelscout.backend.exception.custom.InvalidCredentialsException;
import click.reelscout.backend.factory.UserMapperFactoryRegistry;
import click.reelscout.backend.model.User;
import click.reelscout.backend.repository.UserRepository;
import click.reelscout.backend.s3.S3Service;
import click.reelscout.backend.service.definition.UserService;
import click.reelscout.backend.strategy.UserMapperContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserServiceImplementation implements UserService {
    private final UserRepository userRepository;
    private final UserMapperContext userMapperContext;
    private final UserMapperFactoryRegistry userMapperFactoryRegistry;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;

    @Override
    public UserResponseDTO getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(User.class));

        userMapperContext.setUserMapper(userMapperFactoryRegistry.getMapperFor(user));

        String base64Image = s3Service.getFile(user.getS3ImageKey());

        return userMapperContext.toDto(user, base64Image);
    }

    @Override
    public UserResponseDTO getByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(User.class));

        userMapperContext.setUserMapper(userMapperFactoryRegistry.getMapperFor(user));

        String base64Image = s3Service.getFile(user.getS3ImageKey());

        return userMapperContext.toDto(user, base64Image);
    }

    @Override
    public UserResponseDTO getByUsernameOrEmail(String usernameOrEmail) {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> new EntityNotFoundException(User.class));

        userMapperContext.setUserMapper(userMapperFactoryRegistry.getMapperFor(user));

        String base64Image = s3Service.getFile(user.getS3ImageKey());

        return userMapperContext.toDto(user, base64Image);
    }

    @Override
    public User getCurrentUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user == null) {
            throw new EntityNotFoundException(User.class);
        }

        return user;
    }

    @Override
    public UserResponseDTO getCurrentUserDto() {
        User currentUser = getCurrentUser();

        userMapperContext.setUserMapper(userMapperFactoryRegistry.getMapperFor(currentUser));

        String base64Image = s3Service.getFile(currentUser.getS3ImageKey());

        return userMapperContext.toDto(currentUser, base64Image);
    }

    @Override
    public UserResponseDTO update(UserRequestDTO userRequestDTO) {
        User currentUser = getCurrentUser();

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

        User user = userMapperContext.toEntity(userRequestDTO, s3ImageKey);

        User updatedUser = (User) userMapperContext.toBuilder(user).id(currentUser.getId()).build();

        try {
            userRepository.save(updatedUser);

            s3Service.uploadFile(s3ImageKey, userRequestDTO.getBase64Image());
        } catch (Exception e) {
            throw new EntityUpdateException(User.class);
        }

        return userMapperContext.toDto(updatedUser, userRequestDTO.getBase64Image());
    }
}
