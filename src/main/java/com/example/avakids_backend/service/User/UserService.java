package com.example.avakids_backend.service.User;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.avakids_backend.DTO.User.UserCreateRequest;
import com.example.avakids_backend.DTO.User.UserResponse;
import com.example.avakids_backend.DTO.User.UserUpdateRequest;

public interface UserService {
    List<UserResponse> getAllUsers();

    UserResponse getById(Long id);

    UserResponse createUser(UserCreateRequest dto);

    UserResponse updateUser(Long id, UserUpdateRequest dto, MultipartFile avatar);

    void deleteUser(Long id);

    UserResponse getByToken();
}
