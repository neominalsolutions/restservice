package com.tcell_spring.restservice.infra.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class JsonWebTokenService  implements ITokenService{

    private final String SecretKey = "5513dbd862957660f8baf6039bf6e7abf4a8901af91d32f50504129970f560a5e0891b0c2cd457ab843c8fa5f9f7ca5da5d7edf695c9b17c11a542ab40312831";

    @Override
    public String generateToken(UserDetails userDetails) {

        // token yetkilerin claim olarak eklenmesi
        Map<String, Object> claims = new HashMap<>();

        // Jwt içerisinde authority bilgileri role ve scope ayırılarak ekleniyor

        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> auth.startsWith("ROLE_"))
                .collect(Collectors.joining(","));

        String scopes = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> auth.startsWith("SCOPE_"))
                .collect(Collectors.joining(","));

        userDetails.getAuthorities().forEach(authority -> {
            if(authority.getAuthority().startsWith("ROLE_")) {
                claims.put("role", roles);
            } else if(authority.getAuthority().startsWith("SCOPE_")) {
                claims.put("scope", scopes);
            }
        });


        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .addClaims(claims)// Token sahibi kullanıcı adı
                .setIssuedAt(new Date()) // Token oluşturulma tarihi
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60  * 10)) // 10 dakika geçerli
                .signWith(getSigningKey(),SignatureAlgorithm.HS512) // İmzalama algoritması ve anahtarı
                .compact();
    }

    private Key getSigningKey() {
        // Burada gerçek bir anahtar kullanmalısınız. Bu örnek için basit bir string kullanıldı.
        return Keys.hmacShaKeyFor(SecretKey.getBytes());
    }

    // Verilen token geçerli mi diye kontrol eder
    @Override
    public boolean isExpired(String token) {
        // bu token expire olduysa true döner
        return getClaimsFromToken(token).getExpiration().before(new Date());
    }

    // Token içindeki username bilgisini döner
    @Override
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    @Override
    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
