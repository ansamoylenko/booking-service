package com.samoylenko.bookingservice.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/admin/auth")
public class LoginController {

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }
}

