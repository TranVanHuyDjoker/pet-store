package com.hivetech.controller;

import com.hivetech.model.request.UserInfoRequest;
import com.hivetech.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    //API
    @GetMapping("/api/v1/users/information")
    public ResponseEntity<?> getUserInfo() {
        return ResponseEntity.ok(userService.findByUser());
    }

    @PostMapping("/api/v1/users/information")
    public ResponseEntity<?> getUserInfo(@RequestBody UserInfoRequest request) {
        return ResponseEntity.ok(userService.updateUserInfo(request));
    }

    //VIEW
    @GetMapping("/information")
    public String infoPage(Model model) {
        model.addAttribute("title", "Thông tin người dùng");
        return "user/info-form";
    }
}
