package com.hivetech.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hivetech.utils.enumerates.ColorType;
import com.hivetech.utils.enumerates.Gender;
import com.hivetech.utils.enumerates.PetStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PetDTO implements Serializable     {
    private long id;
    private String name;
    private String primaryPhoto;
    private CategoryDTO category;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate createAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate updateAt;
    private String createdBy;
    private String updatedBy;
    private double price;
    private double salePrice;
    private Gender gender;
    private PetStatus status;
    private ColorType colorType;
    private String description;
}
