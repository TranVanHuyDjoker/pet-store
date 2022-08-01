package com.hivetech.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hivetech.utils.enumerates.ColorType;
import com.hivetech.utils.enumerates.Gender;
import com.hivetech.utils.enumerates.PetStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "pet")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Pet implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    @Enumerated(value = EnumType.STRING)
    private ColorType colorType;
    @Enumerated(value = EnumType.STRING)
    private Gender gender;
    @Enumerated(value = EnumType.STRING)
    private PetStatus status;
    @Column(columnDefinition = "text")
    private String description;
    private double price;
    private double salePrice;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate createAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate updateAt;

}
