package com.hivetech.service;

import com.hivetech.model.dto.*;
import com.hivetech.model.request.OrderRequest;
import com.hivetech.utils.enumerates.OrderStatus;
import com.paypal.api.payments.Payment;
import com.telnyx.sdk.ApiException;
import org.springframework.data.domain.Sort;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface OrderService {
    PageDTO<OrderDTO> getAllOrders(String keyword, int currentPage, int limit, Sort.Direction sortDirection, String sortBy);

    OrderDTO getOrderById(String id);

    OrderDTO createOrderByCod(OrderRequest orderRequest) throws MessagingException, IOException;

    List<ReportOrderForMonth> getOrderForMonth();

    List<ReportOrderForYear> getOrderForYear();

    List<ReportOrderForQuarter> getOrderForQuarter();

    List<ReportOrderForWeek> getOrderForWeek();

    OrderDTO excutePaypal(Payment payment) throws IOException;

    OrderDTO verifyOrder(String code) throws IOException;

    OrderDTO changeOrderStatus(String id, OrderStatus orderStatus);

    OrderDTO resendEmailOrder(String id) throws IOException;

    OrderDTO resendSMSOrder(String id) throws IOException, ApiException;

    List<OrderDTO> getOrderByUser();
}
