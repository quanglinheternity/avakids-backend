package com.example.avakids_backend.repository.Banner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.avakids_backend.entity.Banner;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long>, BannerRepositoryCustom {}
