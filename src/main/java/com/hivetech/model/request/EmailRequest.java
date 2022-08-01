package com.hivetech.model.request;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class EmailRequest {
    @Email(message = "Không đúng định dạng Email")
    private String email;
}
