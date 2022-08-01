package com.hivetech.model.entity.promotion_code;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hivetech.utils.enumerates.PromotionCodeApplyType;
import com.hivetech.utils.enumerates.PromotionCodeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "promotion_code")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionCode implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Enumerated(value = EnumType.STRING)
    private PromotionCodeApplyType codeApplyType;
    @Enumerated(value = EnumType.STRING)
    private PromotionCodeType codeType;
    private String code;
    private double saleAmount;
    private int quantity;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime expiredAt;

}
