package com.tcell_spring.restservice.controller;


import com.tcell_spring.restservice.entity.AppUser;
import com.tcell_spring.restservice.infra.jwt.ITokenService;
import com.tcell_spring.restservice.repository.IAppUserRepository;
import com.tcell_spring.restservice.request.auth.LoginRequest;
import com.tcell_spring.restservice.request.auth.RegisterRequest;
import com.tcell_spring.restservice.response.auth.TokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;
import java.util.Random;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final IAppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private  final ITokenService tokenService;

    public AuthController(IAppUserRepository appUserRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, ITokenService tokenService) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {

//        Random random = new SecureRandom();
//        int index =  random.nextInt(0,100);

        AppUser appUser = new AppUser();
        appUser.setUsername(request.getUsername());
//        appUser.setUsername("user_" + index);
        appUser.setPassword(passwordEncoder.encode(request.getPassword()));
        appUserRepository.save(appUser);

        return ResponseEntity.ok("Kayıt başarılı");
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        // Kimlik doğrulama işlemi
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());

        // Spring Security Authentication Manager ile nasıl çalışır ?
       Authentication auth = authenticationManager.authenticate(authenticationToken);

       if(!auth.isAuthenticated()) {
           return ResponseEntity.status(401).body("Kimlik doğrulama başarısız");
       }

       String accessToken = tokenService.generateToken((UserDetails) auth.getPrincipal());

       // Buradan normal şartlar altında bir JWT token üretilir ve kullanıcıya gönderilir
        return ResponseEntity.ok(new TokenResponse(accessToken,"Bearer"));
    }

}
