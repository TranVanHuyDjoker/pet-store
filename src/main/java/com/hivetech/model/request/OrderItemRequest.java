package com.hivetech.model.request;

import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
public class OrderItemRequest {
    @NotNull(message = "Không tìm thấy thú cưng")
    private long petId;
    @NotNull(message = "Giá thú cưng không được để trống")
    private double price;
}
