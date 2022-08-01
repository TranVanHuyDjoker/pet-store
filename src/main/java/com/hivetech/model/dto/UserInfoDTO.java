package com.hivetech.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO implements Serializable {
    private long id;
    private String firstname;
    private String lastname;
    private String phonenumber;
    private String address;
    private String avatarPath;
    private UserDTO user;
    private long memberPoint;
}
