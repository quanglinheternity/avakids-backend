package com.example.avakids_backend.repository.Payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.avakids_backend.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {}
