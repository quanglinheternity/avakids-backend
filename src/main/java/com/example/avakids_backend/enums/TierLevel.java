package com.example.avakids_backend.enums;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TierLevel {
    BRONZE(1, "Khách hàng mới", BigDecimal.ZERO, new BigDecimal("5000000"), 0, 1.0),

    SILVER(2, "Khách hàng thường xuyên", new BigDecimal("5000000"), new BigDecimal("20000000"), 500, 1.2),

    GOLD(3, "Khách hàng trung thành", new BigDecimal("20000000"), new BigDecimal("50000000"), 1_000, 1.5),

    PLATINUM(4, "Khách hàng giá trị cao", new BigDecimal("50000000"), new BigDecimal("100000000"), 3_000, 2.0),

    DIAMOND(5, "Khách VIP", new BigDecimal("100000000"), BigDecimal.valueOf(Long.MAX_VALUE), 10_000, 3.0);

    private final int levelOrder;
    private final String description;

    /** Tổng tiền tối thiểu */
    private final BigDecimal minTotalSpent;

    /** Tổng tiền tối đa */
    private final BigDecimal maxTotalSpent;

    /** Bonus khi lên hạng */
    private final int upgradeBonus;

    /** Hệ số nhân điểm */
    private final double multiplier;

    public static TierLevel fromTotalSpent(BigDecimal totalSpent) {
        if (totalSpent == null) {
            return BRONZE;
        }

        for (TierLevel tier : values()) {
            if (totalSpent.compareTo(tier.minTotalSpent) >= 0 && totalSpent.compareTo(tier.maxTotalSpent) < 0) {
                return tier;
            }
        }
        return BRONZE;
    }

    public boolean higherThan(TierLevel other) {
        return this.levelOrder > other.levelOrder;
    }

    public boolean lowerThan(TierLevel other) {
        return this.levelOrder < other.levelOrder;
    }
}
