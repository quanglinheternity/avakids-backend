package com.example.avakids_backend.repository.Banner;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.avakids_backend.entity.Banner;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long>, BannerRepositoryCustom {

    List<Banner> findByPositionOrderByDisplayOrderAsc(Banner.BannerPosition position);

    List<Banner> findByIsActiveTrueOrderByDisplayOrderAsc();

    @Query("SELECT b FROM Banner b WHERE b.isActive = true AND " + "(b.startAt IS NULL OR b.startAt <= :now) AND "
            + "(b.endAt IS NULL OR b.endAt >= :now) "
            + "ORDER BY b.displayOrder ASC")
    List<Banner> findActiveBannersByCurrentTime(@Param("now") LocalDateTime now);

    @Query("SELECT b FROM Banner b WHERE b.position = :position AND b.isActive = true AND "
            + "(b.startAt IS NULL OR b.startAt <= :now) AND "
            + "(b.endAt IS NULL OR b.endAt >= :now) "
            + "ORDER BY b.displayOrder ASC")
    List<Banner> findActiveBannersByPositionAndCurrentTime(
            @Param("position") Banner.BannerPosition position, @Param("now") LocalDateTime now);

    List<Banner> findAllByOrderByDisplayOrderAsc();

    boolean existsByPositionAndDisplayOrder(Banner.BannerPosition position, Integer displayOrder);
}
