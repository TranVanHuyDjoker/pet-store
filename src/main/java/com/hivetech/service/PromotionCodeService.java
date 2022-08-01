package com.hivetech.service;

import com.hivetech.model.dto.PromotionCodeDTO;
import com.hivetech.model.request.ApplyPromotionCodeRequest;
import com.hivetech.model.request.PromotionCodeRequest;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface PromotionCodeService {
    PromotionCodeDTO addPromotionCode(PromotionCodeRequest request);

    PromotionCodeDTO updatePromotionCode(long id, PromotionCodeRequest request);

    PromotionCodeDTO getPromotionCodeByCode(String code);

    List<PromotionCodeDTO> getPromotionCode();

    PromotionCodeDTO deletePromotionCodeById(long id);

    PromotionCodeDTO getPromotionCodeById(long id);

    PromotionCodeDTO applyPromotionCode(Authentication authentication, ApplyPromotionCodeRequest request);
}
