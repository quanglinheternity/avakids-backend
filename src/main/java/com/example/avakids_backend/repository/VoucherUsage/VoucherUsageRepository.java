package com.example.avakids_backend.repository.VoucherUsage;


import com.example.avakids_backend.entity.VoucherUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoucherUsageRepository extends JpaRepository<VoucherUsage, Long>,VoucherUsageRepositoryCustom {


    boolean existsByOrderId(Long orderId);
}