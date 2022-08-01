package com.hivetech.schedule;

import com.hivetech.model.entity.promotion_code.PromotionCode;
import com.hivetech.repository.PromotionCodeRepo;
import com.hivetech.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class PromotionCodeSchedule {
    private final PromotionCodeRepo promotionCodeRepo;

    // cron expression (second minute hour dayOfMonth month daysOfWeek)
    @Scheduled(cron = "0 0 0 ? 1-12 MON", zone = "Asia/Bangkok")
    public void deleteExpiredPromotionCode() {
        List<PromotionCode> promotionCodeList = promotionCodeRepo.findAll();
        for (PromotionCode promotionCode : promotionCodeList) {
            if (DateUtils.checkExpiredCode(promotionCode.getExpiredAt())) {
                promotionCodeRepo.delete(promotionCode);
            }
        }
    }
    
}
