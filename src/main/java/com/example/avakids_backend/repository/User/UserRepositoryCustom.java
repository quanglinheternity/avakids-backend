package com.example.avakids_backend.repository.User;

import java.time.LocalDateTime;

public interface UserRepositoryCustom {
    Long countNewUsers(LocalDateTime startDate, LocalDateTime endDate);
}
