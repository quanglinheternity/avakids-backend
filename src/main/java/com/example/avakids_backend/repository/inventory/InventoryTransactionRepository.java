package com.example.avakids_backend.repository.inventory;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.avakids_backend.entity.InventoryTransaction;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {}
