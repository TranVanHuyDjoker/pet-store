package com.hivetech.service.impl;

import com.hivetech.config.exception.BadRequestException;
import com.hivetech.config.exception.NotFoundException;
import com.hivetech.config.security.UserPrincipal;
import com.hivetech.model.dto.PromotionCodeDTO;
import com.hivetech.model.entity.promotion_code.PromotionCode;
import com.hivetech.model.request.ApplyPromotionCodeRequest;
import com.hivetech.model.request.PromotionCodeRequest;
import com.hivetech.repository.PromotionCodeHistoryRepo;
import com.hivetech.repository.PromotionCodeRepo;
import com.hivetech.service.PromotionCodeService;
import com.hivetech.utils.DateUtils;
import com.hivetech.utils.StringUtils;
import com.hivetech.utils.constants.PromotionCodeMessage;
import com.hivetech.utils.enumerates.PromotionCodeApplyType;
import com.hivetech.utils.enumerates.PromotionCodeType;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PromotionCodeServiceImpl implements PromotionCodeService {
    private final ModelMapper modelMapper;
    private final PromotionCodeRepo promotionCodeRepo;
    private final PromotionCodeHistoryRepo promotionCodeHistoryRepo;

    @Override
    public PromotionCodeDTO addPromotionCode(PromotionCodeRequest request) {
        if (DateUtils.validateDate(request.getCreateAt(), request.getExpiredAt())) {
            throw new BadRequestException(PromotionCodeMessage.DATE_BAD_REQUEST);
        }
        if (request.getCodeApplyType().equals(PromotionCodeApplyType.DELIVERY)
                && request.getSaleAmount() > 1.5
                && request.getCodeType().equals(PromotionCodeType.NORMAL)) {
            throw new BadRequestException(PromotionCodeMessage.PRICE_DELIVERY_CODE);
        }
        if (request.getCodeType().equals(PromotionCodeType.PERCENT) && request.getSaleAmount() > 100) {
            throw new BadRequestException(PromotionCodeMessage.PERCENT_PRICE);
        }
        PromotionCode promotionCode = modelMapper.map(request, PromotionCode.class);
        promotionCode.setCode(StringUtils.generateRandomString(10));
        promotionCodeRepo.save(promotionCode);
        return modelMapper.map(promotionCode, PromotionCodeDTO.class);

    }

    @Override
    public PromotionCodeDTO updatePromotionCode(long id, PromotionCodeRequest request) {
        if (DateUtils.validateDate(request.getCreateAt(), request.getExpiredAt())) {
            throw new BadRequestException(PromotionCodeMessage.DATE_BAD_REQUEST);
        }
        if (request.getCodeApplyType().equals(PromotionCodeApplyType.DELIVERY) && request.getSaleAmount() >= 2) {
            throw new BadRequestException(PromotionCodeMessage.PRICE_DELIVERY_CODE);
        }
        PromotionCode promotionCode = promotionCodeRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(PromotionCodeMessage.NOT_FOUND));
        promotionCode.setCodeApplyType(request.getCodeApplyType());
        promotionCode.setCreateAt(request.getCreateAt());
        promotionCode.setExpiredAt(request.getExpiredAt());
        promotionCodeRepo.save(promotionCode);
        return modelMapper.map(promotionCode, PromotionCodeDTO.class);

    }

    @Override
    public PromotionCodeDTO getPromotionCodeByCode(String code) {
        PromotionCode promotionCode = promotionCodeRepo.findByCode(code)
                .orElseThrow(() -> new NotFoundException(PromotionCodeMessage.NOT_FOUND));
        if (DateUtils.checkExpiredCode(promotionCode.getExpiredAt())) {
            promotionCodeRepo.delete(promotionCode);
            throw new BadRequestException(PromotionCodeMessage.EXPIRED_CODE);
        }
        return modelMapper.map(promotionCode, PromotionCodeDTO.class);
    }

    @Override
    public List<PromotionCodeDTO> getPromotionCode() {
        return promotionCodeRepo.findAll().stream()
                .map(code -> modelMapper.map(code, PromotionCodeDTO.class))
                .collect(Collectors.toList());
    }


    @Override
    public PromotionCodeDTO deletePromotionCodeById(long id) {
        PromotionCode promotionCode = promotionCodeRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(PromotionCodeMessage.NOT_FOUND));
        promotionCodeRepo.deleteById(id);
        return modelMapper.map(promotionCode, PromotionCodeDTO.class);
    }

    @Override
    public PromotionCodeDTO getPromotionCodeById(long id) {
        PromotionCode promotionCode = promotionCodeRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(PromotionCodeMessage.NOT_FOUND));
        return modelMapper.map(promotionCode, PromotionCodeDTO.class);
    }

    @Override
    public PromotionCodeDTO applyPromotionCode(Authentication authentication, ApplyPromotionCodeRequest request) {
        PromotionCode promotionCode = promotionCodeRepo.findByCodeAndCodeApplyType(request.getCode(), request.getType())
                .orElseThrow(() -> new NotFoundException(PromotionCodeMessage.NOT_FOUND));
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        if (promotionCodeHistoryRepo.existsByUserAndPromotionCode(userPrincipal.getUser(), promotionCode))
            throw new BadRequestException("Bạn đã sử dụng mã giảm giá này rồi");
        if (DateUtils.checkExpiredCode(promotionCode.getExpiredAt()))
            throw new BadRequestException(PromotionCodeMessage.EXPIRED_CODE);
        if (promotionCode.getQuantity() <= 0)
            throw new BadRequestException(PromotionCodeMessage.QUANTITY_IS_OVER);
        if (promotionCode.getCodeType().equals(PromotionCodeType.NORMAL)
                && promotionCode.getSaleAmount() >= request.getTotalAmount()
                && promotionCode.getCodeApplyType().equals(PromotionCodeApplyType.PET)) {
            throw new BadRequestException(PromotionCodeMessage.CAN_NOT_APPLY);
        }
        return modelMapper.map(promotionCode, PromotionCodeDTO.class);
    }

}
