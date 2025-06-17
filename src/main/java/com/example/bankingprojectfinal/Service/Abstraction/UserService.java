package com.example.bankingprojectfinal.Service.Abstraction;

import com.example.bankingprojectfinal.DTOS.User.PasswordChangeResponse;
import com.example.bankingprojectfinal.DTOS.User.UserChangePasswordRequest;
import com.example.bankingprojectfinal.DTOS.User.UserCreateRequest;
import com.example.bankingprojectfinal.DTOS.User.UserResponse;
import com.example.bankingprojectfinal.DTOS.User.UserStatusChangeRequest;
import org.springframework.data.domain.Page;

public interface UserService {
    UserResponse createGenericUser(UserCreateRequest request);
    UserResponse createAdminUser(UserCreateRequest request);
    UserResponse activateUser(UserStatusChangeRequest request);
    UserResponse disableUser(UserStatusChangeRequest request);
    PasswordChangeResponse changePassword(UserChangePasswordRequest request);
    Page<UserResponse> getAllUsers(Integer page, Integer size);
}