package com.hivetech.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "pet_detail")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PetDetail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    private Pet pet;
    @Column(columnDefinition = "TEXT")
    private String origin;
    private int age;
    private String dadType;
    private String momType;
    private long purebred;
    private String vaccination;
}
