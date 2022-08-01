package com.hivetech.service;

import com.hivetech.model.dto.UserDTO;
import com.hivetech.model.dto.UserInfoDTO;
import com.hivetech.model.entity.User;
import com.hivetech.model.request.UserInfoRequest;
import com.hivetech.model.request.SignUpRequest;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDTO findByUsername(String username);

    List<UserDTO> findAll();

    boolean deleteUser(Long id);

    UserDTO createUser(SignUpRequest request) throws MessagingException, IOException;

    UserDTO updatePassword(String token, String newPassword);

    void createResetToken(String email) throws MessagingException, IOException;

    UserDTO activeUser(String code);

    UserInfoDTO findByUser();

    UserInfoDTO updateUserInfo(UserInfoRequest request);
}
