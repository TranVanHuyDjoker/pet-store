package com.hivetech.model.request;

import com.hivetech.utils.enumerates.DeliveryType;
import com.hivetech.utils.enumerates.PaymentMethod;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
public class OrderRequest {
    @NotBlank(message = "Họ người dùng không được để trống")
    private String firstname;
    @NotBlank(message = "Họ người dùng không được để trống")
    private String lastname;
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0(3\\d|5\\d|7[0|9|7|6|8]|8[1|2|3|4|5|6|8|9]|9\\d)\\d{7}", message = "Sai định dạng điện thoại")
    private String phonenumber;
    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;
    @NotNull(message = "Tổng thành tiền không được để trống")
    private double totalPrice;
    @NotNull(message = "Giá vận chuyển không được để trống")
    private double shipPrice;
    @NotNull(message = "Phương thức thanh toán không được để trống")
    private PaymentMethod paymentMethod;
    @NotNull(message = "Hãng vận chuyển không được để trống")
    private DeliveryType delivery;
    private double salePrice;
    private List<String> promotionCodes;
    private List<OrderItemRequest> orderItems;
}
