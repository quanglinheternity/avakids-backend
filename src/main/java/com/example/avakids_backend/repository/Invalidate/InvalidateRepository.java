package com.example.avakids_backend.repository.Invalidate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.avakids_backend.Entity.InvalidatedToken;

@Repository
public interface InvalidateRepository extends JpaRepository<InvalidatedToken, String> {}
