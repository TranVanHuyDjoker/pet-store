package com.hivetech.model.entity.refund;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "refund_files")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundFile implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String photoPath;
    private String hashingCodeMD5;
    @ManyToOne()
    @JoinColumn(name = "refund_id")
    private Refund refund;
}
