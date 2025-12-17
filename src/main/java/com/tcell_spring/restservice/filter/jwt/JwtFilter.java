package com.tcell_spring.restservice.filter.jwt;


import com.tcell_spring.restservice.infra.jwt.ITokenService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Her istek geldiğinde çalışacak filter -> Token doğrulama işlemleri burada yapılacak
@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final ITokenService tokenService;

    public JwtFilter(ITokenService tokenService) {
        this.tokenService = tokenService;
    }

    // en son bu kısmı doFilterInternal methodunun securityfilter chain öncesi çalışması için securityconfig içinde ayarlamamız gerekiyor.

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Token doğrulama işlemleri burada yapılacak, eğer istek belirli endpointer dışından gelirse doFilterChain ile akış devam etsin
        if(request.getHeader("Authorization") == null) {
            // süreci bir sonraki adıma ilet -> eğer token gönderilmesi gereken bir endpoint'e tokensız istek ayıldıysa securityfilter chain ayarlarında geçemeyiz.
            filterChain.doFilter(request, response);
        } else {

            String authHeader = request.getHeader("Authorization");
            String token = authHeader.replace("Bearer ", "");
            // Token doğrulama işlemleri burada yapılacak
            log.info("Gelen Token: " + token);

            if(tokenService.isExpired(token)){
                filterChain.doFilter(request, response);
                log.info("Token süresi dolmuş.");
            } else {

                try {
                    // getClaimsFromToken method eğer parse edilemez ise token kesinlikle değişmiştir. Yada bizim ürettiğimiz bir token değildir.
                    Claims claims =  tokenService.getClaimsFromToken(token);
                    log.info("Token süresi geçerli.");
                    log.info("Token içindeki username: " + claims.getSubject());
                    // Kullanıcıyı bu bilgilere göre login yapmalıyız
                    // API da Stateless olduğu için her istek geldiğinde kullanıcıyı sistemde login yapmamız gerekiyor.
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(claims.getSubject(), null, null);

                    // API çağrısı yapan kullanıcıyı sistemde login yapıldı.
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } catch (RuntimeException e) {
                    log.error("Token signature doğrulama hatası." + e.getMessage());
                }

                // Kaldığın yerden yoluna devam et
                filterChain.doFilter(request, response);
            }
        }

    }
}
