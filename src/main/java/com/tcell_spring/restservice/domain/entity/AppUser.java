package com.tcell_spring.restservice.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


// UserDetails arayüzünü implemente eden entity sınıfı
// UserDetails arayüzü, Spring Security tarafından kullanıcı bilgilerini temsil etmek için kullanılır

@Data
@Entity
@Table(name = "users")
public class AppUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String username;
    private String password;

    // User ile birlikte Rolleri yüklemek için EAGER fetch tipi kullanıyoruz
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_authorities",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id")
    )
    private List<AppUserAuthority> authorities;

    // ROLE_USER veya SCOPE_READ_POSTS

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities.stream().map(a-> new SimpleGrantedAuthority(a.getName())).toList();
    }


}
