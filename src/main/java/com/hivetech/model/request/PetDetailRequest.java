package com.hivetech.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PetDetailRequest {
    @NotBlank(message = "Xuất xứ thú cưng không được để trống")
    private String origin;
    @NotBlank(message = "Tuổi(tháng) thú cưng không được để trống")
    private int age;
    @NotBlank(message = "Bố thú cưng không được để trống")
    private String dadType;
    @NotBlank(message = "Mẹ thú cưng không được để trống")
    private String momType;
    @NotNull(message = "Độ thuần chủng thú cưng không được để trống")
    private long purebred;
    private String vaccination;
}
