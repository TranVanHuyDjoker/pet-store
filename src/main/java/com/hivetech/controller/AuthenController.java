package com.hivetech.controller;

import com.hivetech.model.dto.UserDTO;
import com.hivetech.model.request.EmailRequest;
import com.hivetech.model.request.UpdatePasswordRequest;
import com.hivetech.model.request.SignUpRequest;
import com.hivetech.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class AuthenController {

    private final UserService userService;
    @PostMapping(value = "/api/v1/auth/signup")
    public ResponseEntity<UserDTO> userRegigter(@Valid @RequestBody SignUpRequest request, BindingResult bindingResult) throws BindException, MessagingException, IOException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PostMapping("/api/v1/auth/forgot")
    public ResponseEntity<String> sendMailVerify(@Valid @RequestBody EmailRequest request, BindingResult bindingResult) throws MessagingException, IOException, BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        userService.createResetToken(request.getEmail());
        return ResponseEntity.ok("Gửi tin nhắn đặt lại mật khẩu thành công. Vui lòng kiểm tra email");
    }

    @PostMapping("/api/v1/auth/reset-password")
    public ResponseEntity<UserDTO> updateUserPassword(@Valid @RequestBody UpdatePasswordRequest request, BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        return ResponseEntity.ok(userService.updatePassword(request.getToken(), request.getPassword()));
    }

    @GetMapping("/signin")
    public String signin(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            model.addAttribute("title", "Đăng nhập");
            return "authen/signin";
        }
        return "redirect:/";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            model.addAttribute("title", "Đăng ký");
            return "authen/signup";
        }

        return "redirect:/";
    }

    @GetMapping("/forgot")
    public String forgotPassword(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            model.addAttribute("title", "Tìm lại mật khẩu");
            return "authen/forgot";
        }
        return "redirect:/";
    }

    @GetMapping("/verify")
    public String verifyCode(@RequestParam String code, Model model) {
        model.addAttribute("message", userService.activeUser(code));
        return "authen/verify";
    }

    @GetMapping("/reset_password")
    public String resetPasswordForm(@RequestParam String code) {
        return "authen/update-password";
    }


}
