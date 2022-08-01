package com.hivetech.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hivetech.utils.enumerates.DeliveryType;
import com.hivetech.utils.enumerates.OrderStatus;
import com.hivetech.utils.enumerates.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO implements Serializable {
    private String id;
    private String fullname;
    private String phonenumber;
    private String address;
    private double totalPrice;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private boolean isPaid;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate createAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate updateAt;
    private DeliveryType delivery;
    private List<OrderItemDTO> orderItems;
}
