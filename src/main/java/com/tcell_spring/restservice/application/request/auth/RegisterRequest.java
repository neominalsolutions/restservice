package com.tcell_spring.restservice.application.request.auth;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
}
