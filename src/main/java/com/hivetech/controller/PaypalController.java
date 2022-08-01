package com.hivetech.controller;

import com.hivetech.config.exception.BadRequestException;
import com.hivetech.model.request.OrderRequest;
import com.hivetech.service.OrderService;
import com.hivetech.service.PaypalService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class PaypalController {
    private final PaypalService paypalService;
    private final OrderService orderService;
    //API
    @GetMapping("/api/v1/paypal/payment/")
    public ResponseEntity<?> getPayment(@RequestParam String paymentId) throws PayPalRESTException {
        return ResponseEntity.ok(paypalService.getPayment(paymentId));
    }

    @GetMapping("/api/v1/paypal/sale")
    public ResponseEntity<?> getSale(@RequestParam String saleId) throws PayPalRESTException {
        return ResponseEntity.ok(paypalService.getSale(saleId));
    }

    @PostMapping("/api/v1/paypal/payment")
    public ResponseEntity<?> createOrderByPaypal(@Valid @RequestBody OrderRequest orderRequest,
                                                 BindingResult bindingResult) throws BindException {
        if(bindingResult.hasErrors()){
            throw new BindException(bindingResult);
        }
        try {
            Payment payment = paypalService.createPayment(orderRequest);
            for(Links link:payment.getLinks()) {
                if(link.getRel().equals("approval_url")) {
                    return ResponseEntity.ok(link.getHref());
                }
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().body("Error");
    }

    //VIEW
    @GetMapping("/paypal/payment/success")
    public String successPaypal(@RequestParam("paymentId") String paymentId,
                                @RequestParam("PayerID") String payerId,Model model) {
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            if (payment.getState().equals("approved")) {
                orderService.excutePaypal(payment);
                return "redirect:/orders/success";
            }
        } catch (PayPalRESTException | IOException e) {
            throw new BadRequestException("Error");
        }
        return "redirect:/";
    }

}
