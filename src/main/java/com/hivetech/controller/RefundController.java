package com.hivetech.controller;

import com.hivetech.model.request.RefundRequest;
import com.hivetech.service.RefundService;
import com.hivetech.utils.enumerates.RefundStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Controller
@RequiredArgsConstructor
public class RefundController {
    private final RefundService refundService;

    //API

    @GetMapping("/api/v1/refunds/{refundId}")

    @PostMapping("/api/v1/refunds/{orderId}")
    public ResponseEntity<?> createRefund(@RequestBody @Valid RefundRequest request,
                                          @PathVariable String orderId) throws IOException, NoSuchAlgorithmException {
        return ResponseEntity.ok(refundService.createRefund(orderId,request));
    }

    @PostMapping("/api/v1/refunds/{refundId}")
    public ResponseEntity<?> createRefund(@PathVariable long refundId) {
//        return ResponseEntity.ok(refundService.updateRefundStatus(refundId, RefundStatus.ACCEPT));
        return null;
    }
    //VIEW
    @GetMapping("/admin/refunds")
    public String getAdminRefundPage(Model model) {
        model.addAttribute("title", "Quản trị: Đơn trả hàng ");
        model.addAttribute("refunds", refundService.getRefunds());
        return "refund/admin/index";
    }
    @GetMapping("/admin/refunds/{refundId}/reply")
    public String getAdminRefundPage(@PathVariable long id,Model model) {
        model.addAttribute("title", "Quản trị: Phản hồi đơn trả hàng " +id);
        return "refund/admin/index";
    }

    @GetMapping("/refunds")
    public String getRefundPage(Model model) {
        model.addAttribute("title", "Đơn trả hàng ");
        model.addAttribute("refunds", refundService.getRefundsByUser());
        return "refund/user/index";
    }

    @GetMapping("/orders/{orderId}/refund")
    public String getRefundAddForm(@RequestParam String orderId, Model model) {
        model.addAttribute("title", "Tạo đơn trả hàng cho đơn " + orderId);
        return "refund/user/form";
    }
}
