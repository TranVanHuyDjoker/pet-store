package com.hivetech.controller;

import com.hivetech.model.dto.DeliveryDTO;
import com.hivetech.model.dto.OrderDTO;
import com.hivetech.model.dto.PageDTO;
import com.hivetech.model.request.OrderRequest;
import com.hivetech.service.DeliveryService;
import com.hivetech.service.OrderService;
import com.hivetech.service.PaypalService;
import com.hivetech.utils.enumerates.OrderStatus;
import com.telnyx.sdk.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final DeliveryService deliveryService;
    private final PaypalService paypalService;

    @GetMapping({"/api/v1/orders"})
    public ResponseEntity<PageDTO<OrderDTO>> getOrders(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        return ResponseEntity.ok(orderService.getAllOrders(keyword, currentPage, limit, sortDirection, sortBy));
    }

    @GetMapping("/api/v1/orders/{id}/items")
    public ResponseEntity<OrderDTO> getOrderItems(@PathVariable String id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/api/v1/users/orders")
    public ResponseEntity<List<OrderDTO>> getOrderByUser() {
        return ResponseEntity.ok(orderService.getOrderByUser());
    }

    @PostMapping("/api/v1/orders/cod")
    public ResponseEntity<?> createOrderByCod(@Valid @RequestBody OrderRequest orderRequest,
                                              BindingResult bindingResult,
                                              HttpServletRequest request,
                                              Model model) throws BindException, MessagingException, IOException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        return ResponseEntity.ok(orderService.createOrderByCod(orderRequest));
    }

    @PutMapping("/api/v1/orders/{id}")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable String id,
                                                      @RequestParam OrderStatus orderStatus) {
        return ResponseEntity.ok(orderService.changeOrderStatus(id, orderStatus));
    }

    @GetMapping("/api/v1/deliveries")
    public ResponseEntity<List<DeliveryDTO>> getDelivery() {
        return ResponseEntity.ok(deliveryService.getDeliveries());
    }

    //VIEW
    @GetMapping("/orders")
    public String userOrderPage(Model model) {
        model.addAttribute("title", "Danh sách đơn hàng");
        model.addAttribute("orders", orderService.getOrderByUser());
        return "order/user/index";
    }

    @GetMapping("/orders/{id}")
    public String userOrderDetailPage(Model model, @PathVariable String id) {
        model.addAttribute("title", "Chi tiết đơn hàng " + id);
        model.addAttribute("order", orderService.getOrderById(id));
        return "order/user/detail";
    }

    @GetMapping("/cart")
    public String cartPage(Model model) {
        model.addAttribute("title", "Giỏ hàng");
        return "cart/index";
    }

    @GetMapping("/checkout")
    public String reviewPage(Model model) {
        model.addAttribute("title", "Thanh toán");
        return "cart/checkout";
    }

    @GetMapping("/orders/verify")
    public String verifyOrder(@RequestParam String code) throws IOException {
        orderService.verifyOrder(code);
        return "redirect:/orders/success";
    }

    @GetMapping("/admin/orders")
    public String getAdminOrderPage(Model model) {
        model.addAttribute("title", "Quản trị: Đơn đặt hàng");
        return "order/admin/index";
    }

    @GetMapping("/admin/orders/{id}")
    public String getAdminOrderItemsPage(Model model, @PathVariable String id) {
        model.addAttribute("title", "Đơn đặt hàng " + id);
        model.addAttribute("order", orderService.getOrderById(id));
        return "order/admin/detail";
    }

    @GetMapping("/orders/success")
    public String getReviewPage(Model model) {
        model.addAttribute("title", "Đặt hàng thành công");
        model.addAttribute("message", "Bạn đã đặt hàng thành công");
        return "order/user/success";
    }

    @GetMapping("/admin/orders/mail/{id}")
    public String resendEmailOrderToUser(@PathVariable String id, Model model) throws IOException {
        orderService.resendEmailOrder(id);
        model.addAttribute("message", "Gửi email order thành công");
        return "order/admin/resend";
    }

    @GetMapping("/admin/orders/sms/{id}")
    public String resendSmsOrderToUser(@PathVariable String id, Model model) throws IOException, ApiException {
        orderService.resendSMSOrder(id);
        model.addAttribute("message", "Gửi SMS order thành công");
        return "order/admin/resend";
    }


}
