package com.hivetech.model.entity;

import com.hivetech.utils.enumerates.GuaranteeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "guarantee")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Guarantee implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private GuaranteeType type;
    @Column(columnDefinition = "TEXT")
    private String description;
}
