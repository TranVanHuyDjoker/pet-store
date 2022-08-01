package com.hivetech.model.dto;

import com.hivetech.utils.enumerates.ColorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO implements Serializable {
    private long id;
    private long petId;
    private String petName;
    private ColorType color;
    private double price;
    private int quantity;
}
