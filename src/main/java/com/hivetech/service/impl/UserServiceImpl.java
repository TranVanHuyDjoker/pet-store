package com.hivetech.service.impl;

import com.hivetech.config.exception.BadRequestException;
import com.hivetech.config.exception.NotFoundException;
import com.hivetech.config.security.UserPrincipal;
import com.hivetech.model.dto.DataMailDTO;
import com.hivetech.model.dto.UserDTO;
import com.hivetech.model.dto.UserInfoDTO;
import com.hivetech.model.entity.Role;
import com.hivetech.model.entity.User;
import com.hivetech.model.entity.UserInfo;
import com.hivetech.model.request.UserInfoRequest;
import com.hivetech.model.request.SignUpRequest;
import com.hivetech.repository.RoleRepo;
import com.hivetech.repository.UserInfoRepo;
import com.hivetech.repository.UserRepo;
import com.hivetech.service.SendMailService;
import com.hivetech.service.UserService;
import com.hivetech.utils.constants.MailConstants;
import com.hivetech.utils.constants.UserConstants;
import com.hivetech.utils.enumerates.RoleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final ModelMapper modelMapper;
    private final PasswordEncoder encoder;
    private final RoleRepo roleRepo;
    private final SendMailService mailService;
    private final UserInfoRepo userInfoRepo;

    @Override
    public UserDTO findByUsername(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(()-> new NotFoundException(UserConstants.NOT_FOUND));
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public List<UserDTO> findAll() {
        return userRepo.findAll().stream().map(u -> modelMapper.map(u, UserDTO.class)).collect(Collectors.toList());
    }


    @Override
    public boolean deleteUser(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new BadRequestException(UserConstants.NOT_FOUND));
        userRepo.delete(user);
        log.info(" Delete user with id: " + id);
        return true;
    }

    @Override
    public UserDTO createUser(SignUpRequest userRequest) throws MessagingException, IOException {
        if (userRepo.existsByUsername(userRequest.getUsername())) {
            throw new BadRequestException("Tên tài khoản này đã tồn tại! ");
        }
        if (userRepo.existsByEmail(userRequest.getEmail())) {
            throw new BadRequestException("Địa chỉ này đã được đăng kí! ");
        }
        User user = modelMapper.map(userRequest, User.class);
        user.setPassword(encoder.encode(userRequest.getPassword()));
        Set<Role> roles = new HashSet<>();
        Role role = roleRepo.findByName(RoleType.USER).orElseThrow(() -> new NotFoundException("Không tìm thấy quyền User"));
        roles.add(role);
        user.setRoles(roles);
        String randomCode = UUID.randomUUID().toString();
        user.setVerificationCode(randomCode);
        user.setStatus(0);
        user = userRepo.save(user);

        String parameter = "/verify?code=" + randomCode;
        DataMailDTO mailDTO = new DataMailDTO();
        Map<String, Object> props = new HashMap<>();
        props.put("email", user.getEmail());
        props.put("parameter", parameter);
        mailDTO.setTo(user.getEmail());
        mailDTO.setSubject(MailConstants.SEND_MAIL_SUBJECT.ClIENT_REGISTER);
        mailDTO.setProps(props);
        mailService.sendMail(mailDTO, MailConstants.TEMPLATE_FILE_NAME.ClIENT_REGISTER);
        log.info(" Insert new User with Infomation:" + user);
        return modelMapper.map(user, UserDTO.class);
    }


    public UserDTO activeUser(String code) {
        User user = userRepo.findByVerificationCode(code)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy người dùng"));
        if (user.getStatus() == 1) {
            return modelMapper.map(user, UserDTO.class);
        }
        user.setVerificationCode(null);
        user.setStatus(1);
        userRepo.save(user);
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public UserInfoDTO findByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication) || authentication instanceof AnonymousAuthenticationToken) {
            throw new BadRequestException("Người dùng chưa đăng nhập");
        }
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserInfo userInfo = userInfoRepo.findByUser(userPrincipal.getUser())
                .orElseThrow(() -> new NotFoundException("Người dùng chưa cập nhật thông tin"));
        return modelMapper.map(userInfo, UserInfoDTO.class);
    }

    @Override
    public UserInfoDTO updateUserInfo(UserInfoRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication) || authentication instanceof AnonymousAuthenticationToken) {
            throw new BadRequestException("Người dùng chưa đăng nhập");
        }
        User user = userRepo.findByUsername(authentication.getName())
                .orElseThrow(() -> new NotFoundException(UserConstants.NOT_FOUND));
        UserInfo userInfo = userInfoRepo.findByUser(user)
                .orElse(null);
        if (Objects.isNull(userInfo)) {
            userInfo = modelMapper.map(request, UserInfo.class);
            userInfo.setUser(user);
            userInfo = userInfoRepo.save(userInfo);
            return modelMapper.map(userInfo, UserInfoDTO.class);
        }
        userInfo.setFirstname(request.getFirstname());
        userInfo.setLastname(request.getLastname());
        userInfo.setAddress(request.getAddress());
        userInfo.setPhonenumber(request.getPhonenumber());
        userInfo.setUser(user);
        userInfo = userInfoRepo.save(userInfo);
        return modelMapper.map(userInfo, UserInfoDTO.class);
    }


    public void createResetToken(String email) throws MessagingException, IOException {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new BadRequestException(UserConstants.NOT_FOUND));
        String token = UUID.randomUUID().toString();
        user.setVerificationCode(token);
        userRepo.save(user);
        String parameter = "/reset_password?code=" + token;
        DataMailDTO mailDTO = new DataMailDTO();
        Map<String, Object> props = new HashMap<>();
        props.put("email", user.getEmail());
        props.put("parameter", parameter);
        mailDTO.setTo(user.getEmail());
        mailDTO.setSubject(MailConstants.SEND_MAIL_SUBJECT.ClIENT_FORGOT);
        mailDTO.setProps(props);
        mailService.sendMail(mailDTO, MailConstants.TEMPLATE_FILE_NAME.ClIENT_FORGOT);
    }

    public UserDTO updatePassword(String token, String newPassword) {
        User user = userRepo.findByVerificationCode(token).orElseThrow(() -> new BadRequestException(UserConstants.NOT_FOUND));
        if (user.getStatus() == 0) {
            throw new BadRequestException("Tài khoản người dùng chưa được kích hoạt");
        }
        user.setVerificationCode(null);
        user.setPassword(encoder.encode(newPassword));
        user = userRepo.save(user);

        return modelMapper.map(user, UserDTO.class);
    }
}
