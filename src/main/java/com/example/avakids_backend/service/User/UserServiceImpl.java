package com.example.avakids_backend.service.User;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.avakids_backend.DTO.User.UserCreateRequest;
import com.example.avakids_backend.DTO.User.UserResponse;
import com.example.avakids_backend.DTO.User.UserUpdateRequest;
import com.example.avakids_backend.entity.User;
import com.example.avakids_backend.enums.RoleType;
import com.example.avakids_backend.mapper.UserMapper;
import com.example.avakids_backend.repository.User.UserRepository;
import com.example.avakids_backend.service.Authentication.auth.AuthenticationService;
import com.example.avakids_backend.util.file.sevrice.FileStorageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;

    private final FileStorageService fileStorageService;
    private final AuthenticationService authenticationService;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toResponseDTO).toList();
    }

    @Override
    public UserResponse getById(Long id) {
        User targetUser = userValidator.validateUserExists(id);
        return userMapper.toResponseDTO(targetUser);
    }

    @Override
    public UserResponse createUser(UserCreateRequest dto) {
        userValidator.validateCreateUser(dto.getEmail(), dto.getPhone());

        User user = userMapper.toEntity(dto);
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

        user.setRole(RoleType.USER);

        return userMapper.toResponseDTO(userRepository.save(user));
    }

    @Override
    public UserResponse updateUser(UserUpdateRequest dto, MultipartFile avatar) {
        Long userId = authenticationService.getCurrentUser().getId();
        User targetUser = userValidator.validateUserExists(userId);

        userMapper.updateUserFromDTO(dto, targetUser);
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            targetUser.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }
        if (avatar != null && !avatar.isEmpty()) {
            fileStorageService.validateImage(avatar);
            fileStorageService.deleteFile(targetUser.getAvatarUrl());
            String avatarUrl = fileStorageService.uploadFile(avatar, "User");
            targetUser.setAvatarUrl(avatarUrl);
        }

        return userMapper.toResponseDTO(userRepository.save(targetUser));
    }

    public void deleteUser(Long id) {
        User user = userValidator.validateUserExists(id);
        userRepository.deleteById(user.getId());
    }

    public UserResponse getByToken() {
        User user = authenticationService.getCurrentUser();
        return userMapper.toResponseDTO(user);
    }
}
