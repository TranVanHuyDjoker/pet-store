package com.hivetech.model.dto;

import com.hivetech.utils.enumerates.DeliveryType;
import lombok.Data;

@Data
public class DeliveryDTO {
    private DeliveryType name;
    private double shipPrice;
}
