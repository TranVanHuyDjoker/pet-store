package com.hivetech.service;

import com.hivetech.config.exception.BadRequestException;
import com.hivetech.config.exception.NotFoundException;
import com.hivetech.config.security.UserPrincipal;
import com.hivetech.model.entity.promotion_code.PromotionCode;
import com.hivetech.model.entity.promotion_code.PromotionCodeHistory;
import com.hivetech.model.request.OrderItemRequest;
import com.hivetech.model.request.OrderRequest;
import com.hivetech.repository.PromotionCodeHistoryRepo;
import com.hivetech.repository.PromotionCodeRepo;
import com.hivetech.utils.constants.PaypalConstant;
import com.hivetech.utils.constants.PromotionCodeMessage;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaypalService {
    private final APIContext apiContext;
    private final PromotionCodeRepo codeRepo;
    private final PromotionCodeHistoryRepo codeHistoryRepo;
    @Value("${base-url}")
    private String baseUrl;


    public Payment createPayment(OrderRequest orderRequest) throws PayPalRESTException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Details details = new Details();
        details.setSubtotal(String.format("%.2f", orderRequest.getTotalPrice()));
        details.setShipping(String.format("%.2f", orderRequest.getShipPrice()));
        details.setShippingDiscount(String.format("%.2f", orderRequest.getSalePrice() * (-1)));

        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setDetails(details);
        amount.setTotal(String.format("%.2f", orderRequest.getTotalPrice() + orderRequest.getShipPrice() - orderRequest.getSalePrice()));
        ItemList itemList = new ItemList();
        List<Item> items = new ArrayList<>();
        for (OrderItemRequest itemRequest : orderRequest.getOrderItems()) {
            Item item = new Item();
            item.setCurrency("USD");
            item.setName(String.valueOf(itemRequest.getPetId()));
            item.setQuantity(String.valueOf(1));
            item.setPrice(String.format("%.2f", itemRequest.getPrice()));
            items.add(item);
        }
        for (String code : orderRequest.getPromotionCodes()) {
            if (!StringUtils.isBlank(code)) {
                PromotionCode promotionCode = codeRepo.findByCode(code)
                        .orElseThrow(() -> new NotFoundException(PromotionCodeMessage.NOT_FOUND));
                if (promotionCode.getQuantity() - 1 <= 0) codeRepo.delete(promotionCode);
                promotionCode.setQuantity(promotionCode.getQuantity() - 1);
                if(codeHistoryRepo.existsByUserAndPromotionCode(userPrincipal.getUser(),promotionCode)){
                   throw new BadRequestException("Người dùng đã sử dụng mã giảm giá này rồi");
                }
                PromotionCodeHistory codeHistory = new PromotionCodeHistory();
                codeHistory.setPromotionCode(promotionCode);
                codeHistory.setUser(userPrincipal.getUser());
                codeHistoryRepo.save(codeHistory);
                codeRepo.save(promotionCode);
            }
        }

        itemList.setItems(items);
        itemList.setShippingPhoneNumber(orderRequest.getPhonenumber());

        List<Transaction> transactions = new ArrayList<>();
        Transaction transaction = new Transaction();
        transaction.setDescription(orderRequest.getAddress());
        transaction.setAmount(amount);
        transaction.setItemList(itemList);
        transaction.setNoteToPayee(orderRequest.getDelivery().toString());
        transaction.setCustom(userPrincipal.getUsername());
        transactions.add(transaction);

        Payment payment = new Payment();
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        payment.setIntent("sale");

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(baseUrl + "/checkout");
        redirectUrls.setReturnUrl(baseUrl + PaypalConstant.SUCCESS_URL);
        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }


    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);
        return payment.execute(apiContext, paymentExecute);
    }

    public Payment getPayment(String paymentId) throws PayPalRESTException {
        return Payment.get(apiContext, paymentId);
    }

    public Refund createPaypalRefund(Sale sale, int percent, String messageResponse) throws PayPalRESTException {
        Amount saleAmount = sale.getAmount();
        Double total = Double.parseDouble(saleAmount.getTotal());
        Double transactionFee = Double.parseDouble(sale.getTransactionFee().getValue());
        Refund refund = new Refund();
        Amount refundAmount = new Amount()
                .setCurrency("USD")
                .setTotal(String.format("%.2f", (total - transactionFee) * percent / 100));
        refund.setDescription(messageResponse);
        refund.setAmount(refundAmount);
        return sale.refund(apiContext, refund);
    }

    public Sale getSale(String saleId) throws PayPalRESTException {
        return Sale.get(apiContext, saleId);
    }

}