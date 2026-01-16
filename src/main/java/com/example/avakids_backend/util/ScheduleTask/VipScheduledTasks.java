package com.example.avakids_backend.util.ScheduleTask;

import com.example.avakids_backend.entity.UserVip;
import com.example.avakids_backend.repository.UserVip.UserVipRepository;
import com.example.avakids_backend.service.UserVip.UserVipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class VipScheduledTasks {

    private final UserVipService vipService;
    private final UserVipRepository vipRepository;


//    @Scheduled(cron = "0 */1 * * * ?")
//    @Scheduled(cron = "0 0 2 * * ?")
// 2:00 AM hàng ngày
//    public void checkVipExpirations() {
//        log.info("Checking VIP expirations...");
//
//        List<UserVip> expiredVips = vipRepository.findByTierExpiresAtLessThanEqual(
//                LocalDateTime.now()
//        );
//
//        expiredVips.forEach(vip -> {
//            vipService.checkAndRenewVipTier(vip.getUserId());
//        });
//
//        log.info("Processed {} VIP records", expiredVips.size());
//    }


}