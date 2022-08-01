package com.hivetech.repository;

import com.hivetech.model.entity.promotion_code.PromotionCode;
import com.hivetech.utils.enumerates.PromotionCodeApplyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PromotionCodeRepo extends JpaRepository<PromotionCode, Long> {
    Optional<PromotionCode> findByCode(String code);

    Optional<PromotionCode> findByCodeAndCodeApplyType(String code, PromotionCodeApplyType codeApplyType);

}
