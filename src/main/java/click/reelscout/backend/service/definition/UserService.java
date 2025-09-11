package click.reelscout.backend.service.definition;

import click.reelscout.backend.dto.request.UserPasswordChangeRequestDTO;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.dto.response.UserLoginResponseDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.model.jpa.User;

import java.util.List;

/**
 * Service definition for user management operations.
 * <p>
 * Provides methods to retrieve users, update profiles, manage suspension and bans,
 * and list users reported by moderators.
 */
public interface UserService <U extends User, R extends UserRequestDTO, S extends UserResponseDTO> {
    /**
     * Get all users.
     *
     * @return list of user DTOs
     */
    List<S> getAll();

    /**
     * Get a user by id.
     *
     * @param id user id
     * @return user DTO
     */
    S getById(Long id);

    /**
     * Get a user by email.
     *
     * @param email user email
     * @return user DTO
     */
    S getByEmail(String email);

    /**
     * Get a user by username.
     *
     * @param username username
     * @return user DTO
     */
    S getByUsername(String username);

    /**
     * Get a user by username or email.
     *
     * @param usernameOrEmail username or email
     * @return user DTO
     */
    S getByUsernameOrEmail(String usernameOrEmail);

    /**
     * Get the DTO for the currently authenticated user.
     *
     * @param authenticatedUser the authenticated user entity
     * @return user DTO
     */
    S getCurrentUserDto(U authenticatedUser);

    /**
     * Update user profile.
     *
     * @param user           the authenticated user entity
     * @param userRequestDTO update payload
     * @return {@link UserLoginResponseDTO} when authentication token needs to be refreshed, otherwise null
     */
    UserLoginResponseDTO update(U user, R userRequestDTO);

    /**
     * Change password for the authenticated user.
     *
     * @param authenticatedUser                 the authenticated user entity
     * @param userPasswordChangeRequestDTO      password change DTO
     * @return {@link CustomResponseDTO} describing the result
     */
    CustomResponseDTO changePassword(U authenticatedUser, UserPasswordChangeRequestDTO userPasswordChangeRequestDTO);

    /**
     * Suspend a user until the specified time.
     *
     * @param userId the id of the user to suspend
     * @param until  suspension end time
     * @param reason reason for suspension
     * @return {@link CustomResponseDTO}
     */
    CustomResponseDTO suspendUser(Long userId, java.time.LocalDateTime until, String reason);

    /**
     * Remove suspension from a user.
     *
     * @param userId the id of the user
     * @return {@link CustomResponseDTO}
     */
    CustomResponseDTO unsuspendUser(Long userId);

    /**
     * Permanently ban a user.
     *
     * @param targetUserId id of the user to ban
     * @param performedBy  the user performing the ban
     * @param reason       reason for ban
     * @return {@link CustomResponseDTO}
     */
    CustomResponseDTO permanentlyBanUser(Long targetUserId, U performedBy, String reason);

    /**
     * Unban a user.
     *
     * @param targetUserId id of the user to unban
     * @param performedBy  the user performing the unban
     * @param reason       reason for unban
     * @return {@link CustomResponseDTO}
     */
    CustomResponseDTO unbanUser(Long targetUserId, U performedBy, String reason);

    /**
     * List users that have been reported by moderators.
     *
     * @return list of user DTOs
     */
    List<S> listUsersReportedByModerators();
}
