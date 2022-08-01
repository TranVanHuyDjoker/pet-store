package com.hivetech.model.request;

import com.hivetech.utils.enumerates.ColorType;
import com.hivetech.utils.enumerates.Gender;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PetRequest {
    @NotBlank(message = "Tên thú cưng không được để trống")
    private String name;
    @NotNull(message = "Danh mục của thú cưng không được để trống")
    private int categoryId;
    @NotNull(message = "Màu thú cưng không được để trống")
    private ColorType color;
    @NotNull(message = "Giới tính thú cưng không được để trống")
    private Gender gender;
    @NotNull(message = "Giá của thú cưng không được để trống")
    private double price;
    private double salePrice;
    private String description;
}
