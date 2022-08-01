package com.hivetech.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hivetech.utils.enumerates.RefundStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RefundDTO {
    private long id;
    private RefundStatus status;
    private String reason;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate createAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate updateAt;
    private String orderId;
}
