package com.hivetech.model.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdatePasswordRequest {
    @NotNull(message = "Đã xảy ra lỗi trong quá trình thực thi")
    private String token;
    @NotNull(message = "Vui lòng nhập mật khẩu mới")
    private String password;
}
