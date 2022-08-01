package com.hivetech.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UserInfoRequest {
    @NotBlank(message = "Họ người dùng không được để trống")
    private String firstname;
    @NotBlank(message = "Tên người dùng không được để trống")
    private String lastname;
    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0(3\\d|5\\d|7[0|9|7|6|8]|8[1|2|3|4|5|6|8|9]|9\\d)\\d{7}", message = "Sai định dạng điện thoại")
    private String phonenumber;
}
