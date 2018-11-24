package com.github.charon.r13b.spring.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import com.github.charon.r13b.spring.entity.User;
import com.github.charon.r13b.spring.security.MyUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("secure")
public class SecureController {
    @GetMapping("user")
    public String user(@AuthenticationPrincipal MyUserDetails userDetails) {
        User user = userDetails.getUser();

        return String.format("Normal User [%s]", user.getName());
    }

    @GetMapping("admin")
    public String admin(@AuthenticationPrincipal MyUserDetails userDetails) {
        User user = userDetails.getUser();

        return String.format("Admin User [%s]", user.getName());
    }

    @GetMapping("me")
    public Map<String, Object> me(@AuthenticationPrincipal MyUserDetails userDetails) {
        Map<String, Object> response = new LinkedHashMap<>();

        User user = userDetails.getUser();

        response.put("email", user.getEmail());
        response.put("name", user.getName());
        response.put("role", user.getRole());

        return response;
    }
}
