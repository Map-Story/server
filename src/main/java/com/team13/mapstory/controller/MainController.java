package com.team13.mapstory.controller;

import com.team13.mapstory.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MainController {

    private final JWTUtil jwtUtil;

    @GetMapping("/")
    public String Main() {
        return "정상";
    }

    @GetMapping("/test")
    public String test(@CookieValue(value = "Authorization") String token) {
        return jwtUtil.getLoginId(token);
    }
}
