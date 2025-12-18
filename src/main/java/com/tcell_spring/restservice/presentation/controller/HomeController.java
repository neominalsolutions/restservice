package com.tcell_spring.restservice.presentation.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/home")
public class HomeController {

    // hasAuthority kullanımında PREFIX olarak ROLE_ veya SCOPE_ eklenmeli
    // @PreAuthorize("hasRole('ADMIN')")
    // @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping// Böyle bir yetkimiz var.
    public String home() {
        return "Welcome to the Home Page!";
    }

}
