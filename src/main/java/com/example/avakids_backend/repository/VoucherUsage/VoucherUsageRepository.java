package com.example.avakids_backend.repository.VoucherUsage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.avakids_backend.entity.VoucherUsage;

@Repository
public interface VoucherUsageRepository extends JpaRepository<VoucherUsage, Long>, VoucherUsageRepositoryCustom {

    boolean existsByOrderId(Long orderId);
}
