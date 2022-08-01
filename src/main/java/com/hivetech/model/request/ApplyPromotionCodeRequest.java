package com.hivetech.model.request;

import com.hivetech.utils.enumerates.PromotionCodeApplyType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ApplyPromotionCodeRequest {
    @NotNull(message = "Vui lòng chọn mã sẽ áp dụng cho thú cưng hay vận chuyển")
    private PromotionCodeApplyType type;
    @NotBlank(message = "Mã không được để trống")
    private String code;
    @NotNull(message = "Giá sản phẩm không được để trống")
    private double totalAmount;
}
