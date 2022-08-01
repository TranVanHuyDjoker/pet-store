package com.hivetech.service;

import com.hivetech.model.dto.RefundDTO;
import com.hivetech.model.request.RefundReplyRequest;
import com.hivetech.model.request.RefundRequest;
import com.hivetech.utils.enumerates.RefundStatus;
import com.telnyx.sdk.ApiException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface RefundService {
    List<RefundDTO> getRefundsByUser();
    RefundDTO createRefund(String orderId,RefundRequest request) throws IOException, NoSuchAlgorithmException;
    List<RefundDTO> getRefunds();
    RefundDTO createRefundReply(long refundId, RefundReplyRequest request) throws IOException, ApiException;
}