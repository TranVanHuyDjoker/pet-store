package com.hivetech.model.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class SignUpRequest {
    @NotBlank(message = "Tên tài khoản không được để trống")
    private String username;
    @NotBlank(message = "Địa chỉ Email không được để trống")
    @Email(message = "Không đúng định dạng email")
    private String email;
    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;
}
