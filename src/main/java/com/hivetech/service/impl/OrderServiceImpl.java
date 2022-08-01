package com.hivetech.service.impl;

import com.hivetech.config.exception.BadRequestException;
import com.hivetech.config.exception.NotFoundException;
import com.hivetech.config.security.UserPrincipal;
import com.hivetech.model.dto.*;
import com.hivetech.model.entity.*;
import com.hivetech.model.entity.promotion_code.PromotionCode;
import com.hivetech.model.entity.promotion_code.PromotionCodeHistory;
import com.hivetech.model.request.OrderItemRequest;
import com.hivetech.model.request.OrderRequest;
import com.hivetech.repository.*;
import com.hivetech.service.OrderService;
import com.hivetech.service.PaypalService;
import com.hivetech.service.SMSService;
import com.hivetech.service.SendMailService;
import com.hivetech.utils.constants.*;
import com.hivetech.utils.enumerates.DeliveryType;
import com.hivetech.utils.enumerates.OrderStatus;
import com.hivetech.utils.enumerates.PaymentMethod;
import com.hivetech.utils.enumerates.PetStatus;
import com.paypal.api.payments.Item;
import com.paypal.api.payments.PayerInfo;
import com.paypal.api.payments.RelatedResources;
import com.paypal.api.payments.Transaction;
import com.telnyx.sdk.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepo orderRepo;
    private final PetRepo petRepo;
    private final PetDetailRepo petDetailRepo;
    private final ModelMapper modelMapper;
    private final OrderItemRepo orderItemRepo;
    private final DeliveryRepo deliveryRepo;
    private final SendMailService sendMailService;
    private final PaypalService paypalService;
    private final UserRepo userRepo;
    private final SMSService smsService;
    private final PaypalPaymentRepo paymentRepo;
    private final PromotionCodeRepo codeRepo;
    private final PromotionCodeHistoryRepo codeHistoryRepo;
    @Value("${prefix}")
    private String orderPrefix;
    @Override
    public PageDTO getAllOrders(String keyword, int currentPage, int limit, Sort.Direction sortDirection, String sortBy) {
        Sort sort = Sort.by(sortDirection, sortBy);
        Pageable pageable = PageRequest.of(currentPage - 1, limit, sort);
        Page<Order> orderPage = orderRepo.paging(keyword, pageable);
        List<OrderDTO> orderDTOList = orderPage.getContent()
                .stream()
                .map(order -> modelMapper.map(order, OrderDTO.class)).collect(Collectors.toList());
        return new PageDTO(orderDTOList,
                limit,
                currentPage,
                orderPage.getTotalPages());
    }

    @Override
    public OrderDTO getOrderById(String id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(OrderConstants.NOT_FOUND));
        List<OrderItem> orderItems = orderItemRepo.findByOrder(order);
        OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);
        orderDTO.setDelivery(order.getDelivery().getName());
        orderDTO.setOrderItems(orderItems.stream()
                .map(item -> modelMapper.map(item, OrderItemDTO.class))
                .collect(Collectors.toList()));
        return orderDTO;
    }


    @Override
    public OrderDTO createOrderByCod(OrderRequest orderRequest) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.getUser();
        Delivery delivery = deliveryRepo.findByName(orderRequest.getDelivery())
                .orElseThrow(() -> new NotFoundException(DeliveryConstrant.NOT_FOUND));

        Order order = modelMapper.map(orderRequest, Order.class);
        String fullname = orderRequest.getFirstname() + " " + orderRequest.getLastname();

        order.setId(generateId());
        order.setStatus(OrderStatus.VERIFY);
        order.setFullname(fullname);
        order.setPaid(false);
        order.setUser(user);
        order.setDelivery(delivery);
        order.setCreateAt(LocalDate.now());
        order.setUpdateAt(LocalDate.now());
        order.setVerifyCode(UUID.randomUUID().toString());
        order = orderRepo.save(order);

        log.info("Create order success:" + order);
        for (OrderItemRequest itemRequest : orderRequest.getOrderItems()) {
            OrderItem item = modelMapper.map(itemRequest, OrderItem.class);
            Pet pet = petRepo.findById(itemRequest.getPetId()).get();
            pet.setStatus(PetStatus.UNAVAILABLE);
            petRepo.save(pet);
            item.setOrder(order);
            item.setPet(pet);
            orderItemRepo.save(item);
        }
            for (String code : orderRequest.getPromotionCodes()) {
                if (!StringUtils.isBlank(code)) {
                    PromotionCode promotionCode = codeRepo.findByCode(code)
                            .orElseThrow(() -> new NotFoundException(PromotionCodeMessage.NOT_FOUND));
                    if (promotionCode.getQuantity() - 1 <= 0) codeRepo.delete(promotionCode);
                    if(codeHistoryRepo.existsByUserAndPromotionCode(userPrincipal.getUser(),promotionCode)){
                        throw new BadRequestException("Người dùng đã sử dụng mã giảm giá này rồi");
                    }
                    PromotionCodeHistory codeHistory = new PromotionCodeHistory();
                    codeHistory.setPromotionCode(promotionCode);
                    codeHistory.setUser(userPrincipal.getUser());
                    codeHistoryRepo.save(codeHistory);
                    codeRepo.save(promotionCode);
                    promotionCode.setQuantity(promotionCode.getQuantity() - 1);
                    codeRepo.save(promotionCode);
                }
            }
        String parameter = "/orders/verify?code=" + order.getVerifyCode();
        DataMailDTO mailDTO = new DataMailDTO();
        Map<String, Object> props = new HashMap<>();
        props.put("fullname", order.getFullname());
        props.put("id", order.getId());
        props.put("totalPrice", order.getTotalPrice());
        props.put("parameter", parameter);

        mailDTO.setTo(user.getEmail());
        mailDTO.setSubject(MailConstants.SEND_MAIL_SUBJECT.ClIENT_ORDER);
        mailDTO.setProps(props);
        sendMailService.sendMail(mailDTO, MailConstants.TEMPLATE_FILE_NAME.CLIENT_ORDER);
        return modelMapper.map(order, OrderDTO.class);
    }

    @Override
    public OrderDTO verifyOrder(String code) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.getUser();
        Order order = orderRepo.findByVerifyCode(code).orElseThrow(() -> new BadRequestException(OrderConstants.NOT_FOUND));
        order.setStatus(OrderStatus.SHIPPING);
        order.setVerifyCode(null);
        List<OrderItem> orderItems = orderItemRepo.findByOrder(order);
        DataMailDTO mailDTO = new DataMailDTO();
        Map<String, Object> props = new HashMap<>();
        props.put("fullname", order.getFullname());
        props.put("id", order.getId());
        props.put("totalPrice", order.getTotalPrice());
        props.put("phonenumber", order.getPhonenumber());
        props.put("address", order.getAddress());
        props.put("createAt", order.getCreateAt());
        for (OrderItem orderItem : orderItems) {
            Pet pet = petRepo.getById(orderItem.getPet().getId());
            orderItem.setOrder(order);
            orderItem.setPet(pet);
            orderItemRepo.save(orderItem);
            props.put("orderitems", orderItems);
        }
        mailDTO.setTo(user.getEmail());
        mailDTO.setSubject(MailConstants.SEND_MAIL_SUBJECT.ClIENT_ORDER_DETAILS);
        mailDTO.setProps(props);
        String pdfFilename = "order" + order.getId() + ".pdf";
        sendMailService.sendMailOrder(mailDTO, MailConstants.TEMPLATE_FILE_NAME.CLIENT_ORDER_DETAILS, pdfFilename);
        orderRepo.save(order);
        return modelMapper.map(order, OrderDTO.class);
    }

    @Override
    public OrderDTO changeOrderStatus(String id, OrderStatus orderStatus) {
        Order order = orderRepo.findById(id).orElseThrow(() -> new NotFoundException(OrderConstants.NOT_FOUND));
        if (order.getStatus().equals(OrderStatus.CANCEL)) {
            throw new BadRequestException("Đơn hàng đã bị huỷ không thể hoàn tác");
        }
        if (!orderStatus.equals(OrderStatus.CANCEL)) {
            order.setStatus(orderStatus);
            order.setUpdateAt(LocalDate.now());
            order = orderRepo.save(order);
        }
        return modelMapper.map(order, OrderDTO.class);
    }

    @Override
    public OrderDTO resendEmailOrder(String id) throws IOException {
        Order order = orderRepo.findById(id).orElseThrow(() -> new BadRequestException(OrderConstants.NOT_FOUND));
        List<OrderItem> orderItems = orderItemRepo.findByOrder(order);
        User user = userRepo.findByUsername(order.getUser().getUsername()).orElseThrow(() -> new BadRequestException(UserConstants.NOT_FOUND));
        DataMailDTO mailDTO = new DataMailDTO();
        Map<String, Object> props = new HashMap<>();
        props.put("fullname", order.getFullname());
        props.put("id", order.getId());
        props.put("totalPrice", order.getTotalPrice());
        props.put("phonenumber", order.getPhonenumber());
        props.put("address", order.getAddress());
        props.put("createAt", order.getCreateAt());
        for (OrderItem orderItem : orderItems) {
            Pet pet = petRepo.getById(orderItem.getPet().getId());
            orderItem.setOrder(order);
            orderItem.setPet(pet);
            orderItemRepo.save(orderItem);
            props.put("orderitems", orderItems);
        }
        mailDTO.setTo(user.getEmail());
        mailDTO.setSubject(MailConstants.SEND_MAIL_SUBJECT.ClIENT_ORDER_DETAILS);
        mailDTO.setProps(props);
        String pdfFilename = "order" + order.getId() + ".pdf";
        sendMailService.sendMailOrder(mailDTO, MailConstants.TEMPLATE_FILE_NAME.CLIENT_ORDER_DETAILS, pdfFilename);
        return modelMapper.map(order, OrderDTO.class);
    }

    @Override
    public OrderDTO resendSMSOrder(String id) throws IOException, ApiException {
        Order order = orderRepo.findById(id).orElseThrow(() -> new BadRequestException(OrderConstants.NOT_FOUND));
        smsService.sendSMS(order.getPhonenumber(),order.toString());
        return modelMapper.map(order, OrderDTO.class);
    }

    @Override
    public List<OrderDTO> getOrderByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication)) throw new BadRequestException("Người dùng chưa đăng nhập");
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        List<Order> orderList = orderRepo.findByUser(userPrincipal.getUser());

        return orderList.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportOrderForMonth> getOrderForMonth() {
        return orderRepo.reportOrderSuccessOfMonth();
    }

    @Override
    public List<ReportOrderForYear> getOrderForYear() {
        return orderRepo.reportOrderSuccessOfYear();
    }

    @Override
    public List<ReportOrderForWeek> getOrderForWeek() {
        return orderRepo.reportOrderSuccessForWeek();
    }

    @Override
    public List<ReportOrderForQuarter> getOrderForQuarter() {
        return orderRepo.reportOrderSuccessOfQuarter();
    }


    public OrderDTO excutePaypal(com.paypal.api.payments.Payment payment) throws IOException {
        PayerInfo payerInfo = payment.getPayer().getPayerInfo();
        Transaction transaction = payment.getTransactions().get(0);
        List<RelatedResources> relatedResources = transaction.getRelatedResources();

        String saleId = relatedResources.get(0).getSale().getId();

        User user = userRepo.findByUsername(transaction.getCustom()).get();
        String fullname = payerInfo.getFirstName() + " " + payerInfo.getLastName();
        Delivery delivery = deliveryRepo
                .findByName(DeliveryType.valueOf(transaction.getNoteToPayee()))
                .orElse(null);

        Order order = Order.builder()
                .id(orderPrefix + com.hivetech.utils.StringUtils.generateRandomString(10))
                .isPaid(true)
                .fullname(fullname)
                .status(OrderStatus.SHIPPING)
                .paymentMethod(PaymentMethod.PAYPAL)
                .totalPrice(Double.parseDouble(transaction.getAmount().getTotal()))
                .user(user)
                .phonenumber(transaction.getItemList().getShippingPhoneNumber())
                .delivery(delivery)
                .address(transaction.getDescription())
                .createAt(LocalDate.now())
                .updateAt(LocalDate.now())
                .build();

        order = orderRepo.save(order);
        for (Item item : transaction.getItemList().getItems()) {
            Pet pet = petRepo.findById(Long.parseLong(item.getName())).get();
            pet.setStatus(PetStatus.UNAVAILABLE);
            petRepo.save(pet);
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .price(Double.parseDouble(item.getPrice()))
                    .pet(pet)
                    .build();
            orderItemRepo.save(orderItem);
        }

        PaypalPayment paymentEntity = new PaypalPayment();
        paymentEntity.setId(payment.getId());
        paymentEntity.setSaleId(saleId);
        paymentEntity.setOrder(order);
        List<OrderItem> orderItems = orderItemRepo.findByOrder(order);
        DataMailDTO mailDTO = new DataMailDTO();
        Map<String, Object> props = new HashMap<>();
        props.put("fullname", order.getFullname());
        props.put("id", order.getId());
        props.put("totalPrice", order.getTotalPrice());
        props.put("phonenumber", order.getPhonenumber());
        props.put("address", order.getAddress());
        for (OrderItem orderItem : orderItems) {
            Pet pet = petRepo.getById(orderItem.getPet().getId());
            orderItem.setOrder(order);
            orderItem.setPet(pet);
            orderItemRepo.save(orderItem);
            props.put("orderitems", orderItems);
        }
        mailDTO.setTo(user.getEmail());
        mailDTO.setSubject(MailConstants.SEND_MAIL_SUBJECT.ClIENT_ORDER_DETAILS);
        mailDTO.setProps(props);
        String pdfFilename = "order" + order.getId() + ".pdf";
        sendMailService.sendMailOrder(mailDTO, MailConstants.TEMPLATE_FILE_NAME.CLIENT_ORDER_DETAILS, pdfFilename);
        paymentRepo.save(paymentEntity);

        return modelMapper.map(order, OrderDTO.class);
    }

    private String generateId(){
        String id = orderPrefix + com.hivetech.utils.StringUtils.generateRandomString(10);
        if(orderRepo.existsById(id)){
            generateId();
        }
        return id;
    }
}
