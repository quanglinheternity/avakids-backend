package com.example.avakids_backend.repository.UserVipSpending6M;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.avakids_backend.entity.UserPointRedemptionLog;

@Repository
public interface UserPointRedemptionLogRepository extends JpaRepository<UserPointRedemptionLog, Long> {
    Optional<UserPointRedemptionLog> findByReferenceIdAndAction(String referenceId, String action);

    boolean existsByReferenceIdAndAction(String referenceId, String action);
}
