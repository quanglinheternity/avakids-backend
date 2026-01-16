package com.example.avakids_backend.service.UserVip;

import org.springframework.stereotype.Component;

import com.example.avakids_backend.entity.UserVip;
import com.example.avakids_backend.exception.AppException;
import com.example.avakids_backend.exception.ErrorCode;
import com.example.avakids_backend.repository.UserVip.UserVipRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserVipValidator {
    private final UserVipRepository vipRepository;

    public UserVip findByUserId(Long userId) {
        return vipRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.USER_VIP_NOT_FOUND));
    }

    public void validateAvailablePoints(Integer availablePoints) {
        if (availablePoints == null || availablePoints <= 1000) {
            throw new AppException(ErrorCode.USER_VIP_POINTS_NOT_FOUND);
        }
    }
}
