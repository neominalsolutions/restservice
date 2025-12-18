package com.tcell_spring.restservice.application.request.auth;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginRequest {

    @JsonProperty("username")
    @Schema(
            description = "Kullanıcı Adı",
            example = "Kaycee_Marvin46"
    )
    private String username;

    @JsonProperty("password")
    @Schema(
            description = "Parola",
            example = "3nKZ6H3SdWzNh3J"
    )
    private String password;
}
