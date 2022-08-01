package com.hivetech.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "photo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Photo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String photoPath;
    private String hashingCodeMD5;
    @ManyToOne()
    @JoinColumn(name = "pet_id")
    private Pet pet;
    //primaryPhoto
    private boolean primaryPhoto;
}
