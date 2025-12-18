package com.tcell_spring.restservice.presentation.config;


import com.tcell_spring.restservice.presentation.filter.jwt.JwtFilter;
import com.tcell_spring.restservice.infra.repository.IAppUserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Slf4j
@EnableMethodSecurity // method seviyesinde güvenlik anotasyonlarını etkinleştirir
@EnableWebSecurity // web güvenliği için gerekli yapılandırmayı etkinleştirir
public class SecurityConfig {

    private final IAppUserRepository appUserRepository;
    private final JwtFilter jwtFilter;

    public SecurityConfig(IAppUserRepository appUserRepository, JwtFilter jwtFilter) {
        this.appUserRepository = appUserRepository;
        this.jwtFilter = jwtFilter;
    }

    // Bazı önemli beanleri tanımlayacağız

    // Kullanıcı detayları repositoryden yükelemek için kullanılan bean
    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return appUserRepository.findByUsername(username);
            }
        };
    }

    // Kullanıcıya ait veritabanı işlemlerini yapacak bean
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var provider =  new DaoAuthenticationProvider(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // Kullanıcı şifrelerini encode/decode etmek için kullanılan bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Kimlik doğrulama işlemlerini yönetecek bean
    @Bean
    public AuthenticationManager authenticationManager() {
        return  authentication -> authenticationProvider().authenticate(authentication);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // api olması sebebi ile csrf kapatıldı
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)) // h2-console frame hatası için eklendi, iframe izin verildi
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/h2-console/**","/api/v1/auth/**")
                    .permitAll()
                    // .requestMatchers("/api/v1/posts/**").hasAuthority("SCOPE_READ_POSTS") // sadece read_posts scope'u olan erişebilir endpoint tanımları yaptık. Route düzeyinde yetkilendirme
                    .anyRequest()
                    .authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS));

        // JwtFilter'ı UsernamePasswordAuthenticationFilter'dan önce ekliyoruz
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        // Authentication Hatalarını handle etme işleme alma gibi bir durumda ortaya çıkacak.
        http.exceptionHandling(exception -> {
            exception.authenticationEntryPoint((request, response, authException) -> {
                log.error(authException.getMessage(), authException);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");

            });
            exception.accessDeniedHandler((request, response, accessDeniedException) -> {
                log.error(accessDeniedException.getMessage(), accessDeniedException);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
            });
        });

        return http.build();
    }


}
