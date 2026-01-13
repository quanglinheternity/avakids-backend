package com.example.avakids_backend.repository.Voucher;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.avakids_backend.entity.Voucher;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long>, VoucherRepositoryCustom {

    boolean existsByCode(String code);
}
