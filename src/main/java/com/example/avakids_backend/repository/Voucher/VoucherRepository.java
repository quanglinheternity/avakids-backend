package com.example.avakids_backend.repository.Voucher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.avakids_backend.entity.Voucher;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long>, VoucherRepositoryCustom {

    Optional<Voucher> findByCode(String code);

    boolean existsByCode(String code);



}
