package com.example.avakids_backend.DTO.UserVip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedeemPreviewResponse {
    private int availablePoints;
    private int pointsWillRedeem;
    private int remainingPoints;
}
