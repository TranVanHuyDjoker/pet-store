package com.hivetech.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hivetech.utils.enumerates.PromotionCodeApplyType;
import com.hivetech.utils.enumerates.PromotionCodeType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PromotionCodeDTO {
    private long id;
    private PromotionCodeApplyType codeApplyType;
    private PromotionCodeType codeType;
    private String code;
    private double saleAmount;
    private int quantity;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy,HH:mm")
    private LocalDateTime createAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy,HH:mm")
    private LocalDateTime expiredAt;
}
