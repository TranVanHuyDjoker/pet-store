package com.hivetech.model.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class RefundRequest {
    @NotBlank(message = "Vui lòng nhập lý do hoàn tiền")
    private String reason;
    @NotNull(message = "Vui lòng nhập hình ảnh thú cưng")
    private MultipartFile[] files;

}
