package com.hivetech.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hivetech.utils.enumerates.OrderStatus;
import com.hivetech.utils.enumerates.PaymentMethod;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "corder")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class Order implements Serializable {
    @Id
    private String id;
    @ManyToOne()
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
    private String fullname;
    private String phonenumber;
    private String address;
    private double totalPrice;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private boolean isPaid;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    private String verifyCode;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "dd/MM/yyyy")
    private LocalDate createAt;
    private LocalDate updateAt;
    private LocalDate guaranteExpiredAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guarantee_id")
    private Guarantee guarantee;


    @Override
    public String toString() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Thông tin đơn hàng ")
                .append("\n Mã đơn hàng: ").append(id)
                .append("\n Tên người nhận: ").append(fullname)
                .append("\n Địa chỉ: ").append(address)
                .append("\n Số điện thoại: ").append(phonenumber)
                .append("\n Tổng số tiền: ").append(totalPrice).append("$")
                .append("\n Tình trạng: ").append(status.equals(OrderStatus.SHIPPING) ? "Đã lên đơn vận chuyển" : "Đang chờ xác nhận")
                .append("\n Thanh toán: ").append(isPaid ? "Đã thanh toán" : "Chưa thanh toán")
                .append("\n Ngày tạo đơn hàng: ").append(createAt.format(dateTimeFormatter))
                .append("\n Hãng vận chuyển: ").append(delivery.getName());

        return stringBuilder.toString();
    }
}
