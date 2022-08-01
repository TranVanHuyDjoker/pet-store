package com.hivetech.repository;

import com.hivetech.model.entity.promotion_code.PromotionCode;
import com.hivetech.model.entity.promotion_code.PromotionCodeHistory;
import com.hivetech.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionCodeHistoryRepo extends JpaRepository<PromotionCodeHistory, Long> {
    PromotionCodeHistory findByPromotionCode(PromotionCode code);
    PromotionCodeHistory findByUser(User user);
    Boolean existsByUserAndPromotionCode(User user, PromotionCode promotionCode);
}
