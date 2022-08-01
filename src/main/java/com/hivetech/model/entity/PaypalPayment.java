package com.hivetech.model.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "paypal_payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class PaypalPayment {
    @Id
    private String id;
    private String saleId;
    private String refundId;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
}
