package com.hivetech.model.entity.refund;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hivetech.model.entity.Order;
import com.hivetech.utils.enumerates.RefundStatus;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "refund")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Refund implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @OneToOne()
    @JoinColumn(name = "order_id")
    private Order order;
    private double refundAmount;
    @Enumerated(value = EnumType.STRING)
    private RefundStatus status;
    @Column(columnDefinition = "TEXT")
    private String reason;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate createAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate updateAt;
}
