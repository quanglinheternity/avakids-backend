package com.example.avakids_backend.service.User;

import org.springframework.stereotype.Component;

import com.example.avakids_backend.Entity.User;
import com.example.avakids_backend.exception.AppException;
import com.example.avakids_backend.exception.ErrorCode;
import com.example.avakids_backend.repository.User.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository userRepository;

    public void validateCreateUser(String email, String phone) {
        validateUserExistsByEmail(email);
        validateUserExistsByPhone(phone);
    }

    public void validateUpdateUser(String email, String phone, Long id) {
        validateUserExistsByEmailAndIdNot(email, id);
        validateUserExistsByPhoneAndIdNot(phone, id);
    }

    public User validateUserExists(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private void validateUserExistsByEmail(String Email) {
        if (userRepository.existsByEmail(Email)) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }

    private void validateUserExistsByPhone(String Phone) {
        if (userRepository.existsByPhone(Phone)) {
            throw new AppException(ErrorCode.PHONE_ALREADY_EXISTS);
        }
    }

    private void validateUserExistsByEmailAndIdNot(String Email, Long id) {
        if (userRepository.existsByEmailAndIdNot(Email, id)) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }

    private void validateUserExistsByPhoneAndIdNot(String Phone, Long id) {
        if (userRepository.existsByPhoneAndIdNot(Phone, id)) {
            throw new AppException(ErrorCode.PHONE_ALREADY_EXISTS);
        }
    }
}
