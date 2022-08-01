package com.hivetech.service.impl;

import com.hivetech.config.exception.BadRequestException;
import com.hivetech.config.exception.NotFoundException;
import com.hivetech.config.security.UserPrincipal;
import com.hivetech.model.dto.RefundDTO;
import com.hivetech.model.entity.Order;
import com.hivetech.model.entity.PaypalPayment;
import com.hivetech.model.entity.refund.Refund;
import com.hivetech.model.entity.refund.RefundFile;
import com.hivetech.model.entity.refund.RefundReply;
import com.hivetech.model.request.RefundReplyRequest;
import com.hivetech.model.request.RefundRequest;
import com.hivetech.repository.OrderRepo;
import com.hivetech.repository.PaypalPaymentRepo;
import com.hivetech.repository.RefundFileRepo;
import com.hivetech.repository.RefundRepo;
import com.hivetech.service.PaypalService;
import com.hivetech.service.RefundService;
import com.hivetech.service.SMSService;
import com.hivetech.utils.UploadUtils;
import com.hivetech.utils.constants.OrderConstants;
import com.hivetech.utils.constants.UserConstants;
import com.hivetech.utils.enumerates.RefundStatus;
import com.paypal.api.payments.Sale;
import com.paypal.base.rest.PayPalRESTException;
import com.telnyx.sdk.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class RefundServiceImpl implements RefundService {
    private final RefundRepo refundRepo;
    private final OrderRepo orderRepo;
    private final ModelMapper modelMapper;
    private final PaypalPaymentRepo paypalPaymentRepo;
    private final PaypalService paypalService;
    private final RefundFileRepo refundFilesRepo;
    private final SMSService smsService;
    @Override
    public List<RefundDTO> getRefundsByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication)) throw new BadRequestException(UserConstants.AUTHEN);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return refundRepo.findByOrderAndMember(userPrincipal.getUser().getId()).stream()
                .map(refund -> {
                    RefundDTO refundDTO = modelMapper.map(refund, RefundDTO.class);
                    refundDTO.setOrderId(refund.getOrder().getId());
                    return refundDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public RefundDTO createRefund(String orderId, RefundRequest request) throws IOException, NoSuchAlgorithmException {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException(OrderConstants.NOT_FOUND));
        if (!Objects.isNull(refundRepo.findByOrder(order))) {
            throw new BadRequestException("Đơn hàng này đã được tạo đơn trả hàng");
        }
        Refund refund = modelMapper.map(request, Refund.class);
        refund.setOrder(order);
        refund.setStatus(RefundStatus.PENDING);
        refund.setCreateAt(LocalDate.now());
        refund.setUpdateAt(LocalDate.now());
        refund = refundRepo.save(refund);
        for (MultipartFile file : request.getFiles()) {
            String hashingCode = UploadUtils.hashByteToMD5(file.getInputStream());
            RefundFile refundFile = new RefundFile();
            refundFile.setRefund(refund);
            refundFile.setHashingCodeMD5(hashingCode);
            refundFile.setPhotoPath(String.format("%s%s", "photo-pet", "/".concat(file.getOriginalFilename())));
            refundFilesRepo.save(refundFile);
        }
        return modelMapper.map(refund, RefundDTO.class);
    }

    @Override
    public List<RefundDTO> getRefunds() {
        return refundRepo.findAll().stream()
                .map(refund -> {
                    RefundDTO refundDTO = modelMapper.map(refund, RefundDTO.class);
                    refundDTO.setOrderId(refund.getOrder().getId());
                    return refundDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public RefundDTO createRefundReply(long refundId, RefundReplyRequest request) throws IOException, ApiException {
        Refund refund = refundRepo.findById(refundId)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy đơn hoàn tiền"));
        Order order = refund.getOrder();
        refund.setStatus(request.getStatus());
        refund = refundRepo.save(refund);
        RefundReply refundReply = new RefundReply();
        refundReply.setRefund(refund);
        refundReply.setRefundAmount(order.getTotalPrice() * request.getPercent() / 100);
        refundReply.setReply(request.getReply());
        smsService.sendSMS(order.getPhonenumber(),
                "Dựa vào tình trạng thú cưng, chúng tôi sẽ hoàn lại cho bạn " + order.getTotalPrice() + "$. Bạn đồng ý chứ?");
        return modelMapper.map(refund,RefundDTO.class);
    }

    private String refundWithCodMethod(Refund refund, double totalPrice, int percent) {
        refund.setStatus(RefundStatus.ACCEPT);
        refund.setRefundAmount(totalPrice * percent / 100);
        refund.setUpdateAt(LocalDate.now());
        refundRepo.save(refund);
        return "Hoàn trả tiền thành công";
    }

    private String refundWithPaypalMethod(Refund refund, Order order, int percent, String messageResponse) {
        PaypalPayment paypalPayment = paypalPaymentRepo.findByOrder(order).get();
        try {
            Sale paypalSale = paypalService.getSale(paypalPayment.getSaleId());
            if (paypalSale.getState().equals("pending"))
                throw new BadRequestException("Đơn hàng đang được Paypal xử lý");
            com.paypal.api.payments.Refund paypalRefund = paypalService.createPaypalRefund(paypalSale, percent, messageResponse);
            paypalPayment.setRefundId(paypalRefund.getId());
            paypalPaymentRepo.save(paypalPayment);

            return "Đơn hàng đã được hoàn tiền";
        } catch (PayPalRESTException e) {
            log.error("Lỗi Paypal {}", e);
        }
        return "Hoàn tiền cho đơn hàng thất bại";
    }
}
