package com.example.PG.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/") // 루트 URL 요청 처리
    public String home() {
        
        return "basic/main-home";
    }
}
