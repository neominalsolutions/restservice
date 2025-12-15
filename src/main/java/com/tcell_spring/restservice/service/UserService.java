package com.tcell_spring.restservice.service;


import com.tcell_spring.restservice.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// streotype.Service annotation is used to indicate that this class is a service component in the Spring framework.
// otomatik bean taraması (component scanning) sırasında bu sınıfın bir bean olarak tanımlanmasını sağlar.

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    // Dependency Injection (DI) ile UserRepository bean'ini alıyoruz.
    // UserRepository userRepository -> Constructor injection ile UserService bean ayağa kaldırılırken aslında UserRepository bean'i de enjekte edilir. Servise bağımlılık olarak eklenmiş olur.
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getUserInfo() {
        log.info("getUserInfo");
        return userRepository.getUserByName("Alice");
    }
}


