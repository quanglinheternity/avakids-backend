package com.example.avakids_backend.service.UserVip;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.avakids_backend.DTO.UserVip.RedeemPreviewResponse;
import com.example.avakids_backend.entity.UserPointRedemptionLog;
import com.example.avakids_backend.entity.UserVip;
import com.example.avakids_backend.entity.UserVipSpending6M;
import com.example.avakids_backend.enums.TierLevel;
import com.example.avakids_backend.repository.UserVip.UserVipRepository;
import com.example.avakids_backend.repository.UserVipSpending6M.UserPointRedemptionLogRepository;
import com.example.avakids_backend.repository.UserVipSpending6M.UserVipSpending6MRepository;
import com.example.avakids_backend.service.Authentication.auth.AuthenticationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserVipServiceImpl implements UserVipService {

    private final UserVipRepository vipRepository;
    private final UserVipSpending6MRepository userVipSpending6MRepository;
    private final AuthenticationService authenticationService;
    private final UserVipValidator userVipValidator;
    private final UserPointRedemptionValidator userPointRedemptionValidator;
    private final UserPointRedemptionLogRepository userPointRedemptionLogRepository;
    private static final BigDecimal POINTS_1_PER_VND = new BigDecimal("1000"); // 1 điểm cho với số tiền

    private static final int VIP_TIER_DURATION_MONTHS = 12; // VIP có hiệu lực 12 tháng

    @Override
    @Transactional
    public void processOrderCompletion(Long userId, Long orderId, BigDecimal orderAmount) {
        LocalDateTime now = LocalDateTime.now();
        UserVip vip = getOrCreateVip(userId);

        // Tính điểm thưởng
        int pointsEarned = calculatePoints(orderAmount, vip);

        // Cập nhật thông tin
        vip.setTotalSpent(vip.getTotalSpent().add(orderAmount));
        vip.setTotalUpgrades(vip.getTotalUpgrades().add(orderAmount));
        vip.setTotalPoints(vip.getTotalPoints() + pointsEarned);
        vip.setAvailablePoints(vip.getAvailablePoints() + pointsEarned);

        // Kiểm tra nâng hạng VIP
        checkAndUpgradeTier(vip);

        vipRepository.save(vip);
        addOrderTo6M(userId, orderAmount, now);

        log.info("User {} earned {} points from order {}", userId, pointsEarned, orderId);
    }

    public RedeemPreviewResponse previewRedeemPoints(BigDecimal orderAmount) {

        Long userId = authenticationService.getCurrentUser().getId();
        UserVip vip = userVipValidator.findByUserId(userId);

        Integer availablePoints = vip.getAvailablePoints();
        if (availablePoints == null || availablePoints <= 0) {
            return RedeemPreviewResponse.builder()
                    .availablePoints(0)
                    .pointsWillRedeem(0)
                    .remainingPoints(0)
                    .build();
        }

        BigDecimal availablePointsBD = BigDecimal.valueOf(availablePoints);

        BigDecimal pointsToRedeemBD = orderAmount.compareTo(availablePointsBD) > 0 ? availablePointsBD : orderAmount;

        int pointsToRedeem = pointsToRedeemBD.intValue();

        return RedeemPreviewResponse.builder()
                .availablePoints(availablePoints)
                .pointsWillRedeem(pointsToRedeem)
                .remainingPoints(availablePoints - pointsToRedeem)
                .build();
    }

    @Override
    @Transactional
    public BigDecimal redeemPoints(Long userId, BigDecimal orderAmount, String orderCode) {
        UserVip vip = userVipValidator.findByUserId(userId);

        Integer availablePoints = vip.getAvailablePoints();

        userVipValidator.validateAvailablePoints(availablePoints);

        BigDecimal availablePointsBD = BigDecimal.valueOf(availablePoints);

        BigDecimal pointsToRedeemBD;
        if (orderAmount.compareTo(availablePointsBD) > 0) {
            // order > point → dùng hết point
            pointsToRedeemBD = availablePointsBD;
        } else {
            // order <= point → dùng đúng bằng order
            pointsToRedeemBD = orderAmount;
        }

        int pointsToRedeem = pointsToRedeemBD.intValue();

        vip.setAvailablePoints(availablePoints - pointsToRedeem);
        vipRepository.save(vip);

        // Log
        logRedemption(userId, pointsToRedeem, vip, "REDEEM", orderCode);
        return pointsToRedeemBD;
    }

    @Override
    @Transactional
    public void refundPoints(Long userId, BigDecimal totalAmount, String orderCode) {
        if (userPointRedemptionValidator.isRefunded(orderCode)) {
            return;
        }
        int refundFromAmount = totalAmount == null
                ? 0
                : totalAmount.setScale(0, RoundingMode.HALF_UP).intValue();

        UserPointRedemptionLog redeemLog = userPointRedemptionValidator.getRedeemLogOrThrow(orderCode);
        int pointsUsed = 0;
        if (redeemLog != null && redeemLog.getPointsUsed() != null) {
            pointsUsed = redeemLog.getPointsUsed();
        }
        if (pointsUsed == 0 && refundFromAmount == 0) {
            return;
        }
        int totalRefundPoints = pointsUsed + refundFromAmount;

        UserVip vip = getOrCreateVip(userId);
        vip.setAvailablePoints(vip.getAvailablePoints() + totalRefundPoints);
        vipRepository.save(vip);

        logRedemption(userId, totalRefundPoints, vip, "REFUND", orderCode);
    }

    //     Kiểm tra và gia hạn VIP tier
    @Transactional
    public void checkAndRenewVipTier(Long userId) {
        UserVip vip = userVipValidator.findByUserId(userId);

        LocalDateTime now = LocalDateTime.now();

        boolean isExpired =
                vip.getTierExpiresAt() != null && vip.getTierExpiresAt().isBefore(now);

        boolean isExpiringSoon =
                vip.getTierExpiresAt() != null && vip.getTierExpiresAt().isBefore(now.plusDays(30));

        boolean eligible = isEligibleForTierRenewal(vip, userId);

        // Gia hạn VIP
        if (isExpiringSoon && eligible) {
            resetSpending6M(userId, now.plusMonths(6), now.plusMonths(12));

            vip.setTierExpiresAt(calculateNewExpiryDate(vip.getTierExpiresAt()));

            vipRepository.save(vip);
            log.info("VIP renewed for user {}", userId);
            return;
        }

        // Hết hạn và không đủ điều kiện → downgrade
        if (isExpired && !eligible) {
            resetSpending6M(userId, now.plusMonths(6), now.plusMonths(12));

            downgradeTier(vip);
            vipRepository.save(vip);
            log.info("VIP downgraded for user {}", userId);
        }
    }

    private int calculatePoints(BigDecimal orderAmount, UserVip vip) {

        BigDecimal eligibleAmount = orderAmount.multiply(new BigDecimal("0.9")); // Giả sử 90% là giá trị sản phẩm

        int points =
                eligibleAmount.divide(POINTS_1_PER_VND, 0, RoundingMode.DOWN).intValue();

        double multiplier = vip.getTierLevel().getMultiplier();
        points = (int) (points * multiplier);

        return Math.max(points, 1);
    }

    private void checkAndUpgradeTier(UserVip vip) {
        TierLevel currentTier = vip.getTierLevel();
        TierLevel newTier = TierLevel.fromTotalSpent(vip.getTotalUpgrades());

        if (newTier.ordinal() > currentTier.ordinal()) {
            vip.setTierLevel(newTier);

            LocalDateTime expiryDate = LocalDateTime.now().plusMonths(VIP_TIER_DURATION_MONTHS);
            vip.setTierExpiresAt(expiryDate);

            // Thưởng điểm khi nâng hạng
            int upgradeBonus = calculateUpgradeBonus(currentTier, newTier);
            vip.setAvailablePoints(vip.getAvailablePoints() + upgradeBonus);
            vip.setTotalPoints(vip.getTotalPoints() + upgradeBonus);
            resetSpending6M(vip.getUserId(), expiryDate.minusMonths(6), expiryDate);

            // Gửi thông báo nâng hạng

            log.info(
                    "User {} upgraded from {} to {} with {} bonus points",
                    vip.getUserId(),
                    currentTier,
                    newTier,
                    upgradeBonus);
        }
    }

    private int calculateUpgradeBonus(TierLevel fromTier, TierLevel toTier) {
        if (toTier.higherThan(fromTier)) {
            return toTier.getUpgradeBonus();
        }
        return 0;
    }

    private void downgradeTier(UserVip vip) {
        TierLevel currentTier = vip.getTierLevel();
        TierLevel newTier = TierLevel.BRONZE;
        vip.setTotalUpgrades(new BigDecimal("0"));
        vip.setTierLevel(newTier);
        vip.setTierExpiresAt(null);

        log.info("User {} downgraded from {} to {}", vip.getUserId(), currentTier, newTier);
    }

    private UserVip getOrCreateVip(Long userId) {
        return vipRepository.findByUserId(userId).orElseGet(() -> {
            UserVip newVip = UserVip.builder()
                    .userId(userId)
                    .totalPoints(0)
                    .availablePoints(0)
                    .totalSpent(BigDecimal.ZERO)
                    .totalUpgrades(BigDecimal.ZERO)
                    .tierLevel(TierLevel.BRONZE)
                    .build();
            return vipRepository.save(newVip);
        });
    }

    private boolean isEligibleForTierRenewal(UserVip vip, Long userId) {

        UserVipSpending6M userVipSpending6M =
                userVipSpending6MRepository.findByUserId(userId).orElseThrow();
        BigDecimal minSpentForRenewal = vip.getTierLevel().getMinTotalSpent();
        return userVipSpending6M.getTotalSpent6m().compareTo(minSpentForRenewal) >= 0;
    }

    private LocalDateTime calculateNewExpiryDate(LocalDateTime currentExpiry) {
        return currentExpiry.plusMonths(VIP_TIER_DURATION_MONTHS);
    }

    private void logRedemption(Long userId, int points, UserVip vip, String action, String orderCode) {

        UserPointRedemptionLog log = new UserPointRedemptionLog();
        log.setUserId(userId);
        log.setPointsUsed(points);
        log.setVipTier(vip.getTierLevel().name());
        log.setAction(action);
        log.setReferenceId(orderCode);
        log.setCreatedAt(LocalDateTime.now());

        userPointRedemptionLogRepository.save(log);
    }

    private void resetSpending6M(Long userId, LocalDateTime tierStartAt, LocalDateTime tierExpiresAt) {
        UserVipSpending6M spending = userVipSpending6MRepository
                .findByUserId(userId)
                .orElseGet(() -> {
                    UserVipSpending6M s = new UserVipSpending6M();
                    s.setUserId(userId);
                    return s;
                });

        spending.setTotalSpent6m(BigDecimal.ZERO);
        spending.setOrderCount6m(0);
        spending.setLastOrderAt(null);

        spending.setPeriodStart(tierStartAt != null ? tierStartAt.toLocalDate() : null);
        spending.setPeriodEnd(tierExpiresAt != null ? tierExpiresAt.toLocalDate() : null);

        userVipSpending6MRepository.save(spending);
    }

    @Transactional
    public void addOrderTo6M(Long userId, BigDecimal orderAmount, LocalDateTime orderCompletedAt) {
        UserVipSpending6M spending =
                userVipSpending6MRepository.findByUserId(userId).orElseThrow();

        LocalDate orderDate = orderCompletedAt.toLocalDate();

        if (orderDate.isAfter(spending.getPeriodEnd())) {

            spending.setTotalSpent6m(BigDecimal.ZERO);
            spending.setOrderCount6m(0);
        }

        boolean inPeriod =
                !orderDate.isBefore(spending.getPeriodStart()) && !orderDate.isAfter(spending.getPeriodEnd());

        if (!inPeriod) {
            return;
        }

        spending.setTotalSpent6m(spending.getTotalSpent6m().add(orderAmount));

        spending.setOrderCount6m(spending.getOrderCount6m() + 1);

        spending.setLastOrderAt(orderCompletedAt);

        userVipSpending6MRepository.save(spending);
    }
}
