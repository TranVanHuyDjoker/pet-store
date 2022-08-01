package com.hivetech.model.entity.refund;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "refund_reply")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundReply implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(columnDefinition = "text")
    private String reply;
    private double refundAmount;
    @OneToOne()
    @JoinColumn(name = "refund_id")
    private Refund refund;
}
