package com.github.charon.r13b.spring.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("public")
public class PublicController {
    @GetMapping
    public String message() {
        return "Hello World!!";
    }
}
