package com.hivetech.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hivetech.utils.enumerates.PromotionCodeApplyType;
import com.hivetech.utils.enumerates.PromotionCodeType;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class PromotionCodeRequest {
    @NotNull(message = "Loại mã giảm giá không được để trống")
    private PromotionCodeType codeType;
    @NotNull(message = "Mã áp dụng không được để trống")
    private PromotionCodeApplyType codeApplyType;
    @NotNull(message = "Giảm giá theo mã không được để trống")
    private double saleAmount;
    @NotNull(message = "Số lượng mã không được để trống")
    private int quantity;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyy, HH:mm:ss")
    @NotNull(message = "Ngày mã bắt đầu không được để trống")
    private LocalDateTime createAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyy, HH:mm:ss")
    @NotNull(message = "Ngày mã hết hạn không được để trống")
    private LocalDateTime expiredAt;
}
