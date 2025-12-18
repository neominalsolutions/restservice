package com.tcell_spring.restservice.application.request.auth;


import lombok.Data;

@Data
public class LoginRequest {

    private String username;
    private String password;
}
