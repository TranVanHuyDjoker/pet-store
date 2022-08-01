package com.hivetech.model.request;

import com.hivetech.utils.enumerates.RefundStatus;
import lombok.Data;
@Data
public class RefundReplyRequest {
    private RefundStatus status;
    private String reply;
    private Integer percent;
}
