package com.tcell_spring.restservice.infra.jwt;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

public interface ITokenService {

    // UserDetails bilgilerine göre bir token oluşturur
    String generateToken(UserDetails userDetails);
    // Verilen token geçerli mi diye kontrol eder
    boolean isExpired(String token);
    // Token içindeki username bilgisini döner
    String getUsernameFromToken(String token);
    Claims getClaimsFromToken(String token);

}

// Claims Nedir ? Kullanıcıya token içinde gömülü olarak gönderilen bilgiler
// Claims -> subject -> username, roles: ROLE_USER, ROLE_ADMIN, exp, iat, scope: read, write

// JWT Token Yapısı -> alg, typ -> header
// payload -> claims
// signature -> secret key ile imzalanmış kısım , HS256, HS512 (shared key), RS256, RS512 (private ve public key)
// Eğer token imzası geçersiz ise token manipüle edilmiş demektir.