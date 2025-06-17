package com.example.bankingprojectfinal.Service.Concrete;

import com.example.bankingprojectfinal.DTOS.User.PasswordChangeResponse;
import com.example.bankingprojectfinal.DTOS.User.UserChangePasswordRequest;
import com.example.bankingprojectfinal.DTOS.User.UserCreateRequest;
import com.example.bankingprojectfinal.DTOS.User.UserResponse;
import com.example.bankingprojectfinal.DTOS.User.UserStatusChangeRequest;
import com.example.bankingprojectfinal.Exception.DuplicateResourceException;
import com.example.bankingprojectfinal.Exception.ResourceNotFoundException;
import com.example.bankingprojectfinal.Exception.UnauthorizedException;
import com.example.bankingprojectfinal.Model.Entity.UserEntity;
import com.example.bankingprojectfinal.Model.Enums.UserRole;
import com.example.bankingprojectfinal.Model.Enums.UserStatus;
import com.example.bankingprojectfinal.Repository.UserRepository;
import com.example.bankingprojectfinal.Service.Abstraction.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private UserResponse convertToUserResponse(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        return UserResponse.builder()
                .username(userEntity.getUsername())
                .role(userEntity.getRole())
                .status(userEntity.getStatus())
                .registrationDate(userEntity.getRegistrationDate())
                .build();
    }

    private UserResponse createUser(UserCreateRequest request, UserRole role) {
        log.info("Attempting to create a {} user with username: {}", role.name(), request.getUsername());

        if (userRepository.existsById(request.getUsername())) {
            log.warn("User creation failed: Username '{}' already exists.", request.getUsername());
            throw new DuplicateResourceException("User with username " + request.getUsername() + " already exists.");
        }

        UserEntity userEntity = UserEntity.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .role(role)
                .status(UserStatus.ACTIVE)
                .registrationDate(LocalDate.now())
                .build();

        UserEntity savedUser = userRepository.save(userEntity);
        log.info("{} user created successfully with username: {}", role.name(), savedUser.getUsername());
        return convertToUserResponse(savedUser);
    }

    @Override
    @Transactional
    public UserResponse createGenericUser(UserCreateRequest request) {
        return createUser(request, UserRole.USER);
    }

    @Override
    @Transactional
    public UserResponse createAdminUser(UserCreateRequest request) {
        return createUser(request, UserRole.ADMIN);
    }


    private UserResponse updateUserStatus(UserStatusChangeRequest request, UserStatus newStatus) {
        String username = request.getUsername();
        log.info("Attempting to change status of user '{}' to {}", username, newStatus.name());

        UserEntity userEntity = userRepository.findById(username)
                .orElseThrow(() -> {
                    log.warn("Status change failed: User '{}' not found.", username);
                    return new ResourceNotFoundException("User not found with username: " + username);
                });

        if (userEntity.getStatus().equals(newStatus)) {
            log.warn("User '{}' is already in status {}.", username, newStatus);
            throw new DuplicateResourceException("User is already in status: " + newStatus.name());
        }

        userEntity.setStatus(newStatus);
        UserEntity updatedUser = userRepository.save(userEntity);
        log.info("User '{}' status updated to {}", updatedUser.getUsername(), newStatus.name());
        return convertToUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public UserResponse activateUser(UserStatusChangeRequest request) {
        return updateUserStatus(request, UserStatus.ACTIVE);
    }

    @Override
    @Transactional
    public UserResponse disableUser(UserStatusChangeRequest request) {
        return updateUserStatus(request, UserStatus.DISABLED);
    }

    @Override
    @Transactional
    public PasswordChangeResponse changePassword(UserChangePasswordRequest request) {
        String username = request.getUsername();
        log.info("Attempting to change password for user: {}", username);

        UserEntity userEntity = userRepository.findById(username)
                .orElseThrow(() -> {
                    log.warn("Password change failed: User '{}' not found.", username);
                    return new ResourceNotFoundException("User not found with username: " + username);
                });

        if (!userEntity.getPassword().equals(request.getOldPassword())) {
            log.warn("Password change failed for user '{}': Incorrect old password.", username);
            throw new UnauthorizedException("Incorrect old password for user: " + username);
        }

        if (userEntity.getPassword().equals(request.getNewPassword())) {
            log.warn("Password change failed for user '{}': New password is the same as old password.", username);
            return PasswordChangeResponse.builder()
                    .success(false)
                    .message("New password cannot be the same as the old password.")
                    .build();
        }

        userEntity.setPassword(request.getNewPassword());
        userRepository.save(userEntity);

        log.info("Password changed successfully for user: {}", username);
        return PasswordChangeResponse.builder()
                .success(true)
                .message("Password changed successfully.")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Integer page, Integer size) {
        log.info("Fetching all users (page: {}, size: {})", page, size);
        PageRequest pageable = PageRequest.of(page, size, Sort.by("username").ascending());

        Page<UserEntity> userEntities = userRepository.findAll(pageable);

        return userEntities.map(this::convertToUserResponse);
    }
}