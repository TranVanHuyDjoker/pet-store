package com.hivetech.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping({"/admin"})
    public String adminHome(Model model) {
        model.addAttribute("title", "Quản trị: Trang chủ");
        return "/admin-index";
    }

    @GetMapping({""})
    public String home(Model model) {
        model.addAttribute("title", " Trang chủ");
        return "/user-index";
    }
}
