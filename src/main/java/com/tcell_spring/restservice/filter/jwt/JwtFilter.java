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

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final ITokenService tokenService;
    private final AuthenticationManager authenticationManager;

    public JwtFilter(ITokenService tokenService, AuthenticationManager authenticationManager) {
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
    }

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
                Claims claims =  tokenService.getClaimsFromToken(token);
                log.info("Token süresi geçerli.");
                log.info("Token içindeki username: " + claims.getSubject());

                // Kullanıcıyı bu bilgilere göre login yapmalıyız
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(claims.getSubject(), null, null);

                // API çağrısı yapan kullanıcıyı sistemde login yapıldı.
                Authentication auth = authenticationManager.authenticate(authenticationToken);
                SecurityContextHolder.getContext().setAuthentication(auth);
                // Kaldığın yerden yoluna devam et
                filterChain.doFilter(request, response);
            }
        }

    }
}
