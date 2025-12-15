package com.tcell_spring.restservice.service;


import com.tcell_spring.restservice.infra.IEmailSender;
import com.tcell_spring.restservice.infra.SendGridEmailSender;
import com.tcell_spring.restservice.infra.TurkcellEmailSender;
import com.tcell_spring.restservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

// streotype.Service annotation is used to indicate that this class is a service component in the Spring framework.
// otomatik bean taraması (component scanning) sırasında bu sınıfın bir bean olarak tanımlanmasını sağlar.

@Service
@Slf4j
public class UserService {

    // turkcellEmailSender -> Bean Spring Context kaydolurken bir servis bu bean isimleri ile register oluyor
    private final UserRepository userRepository;
    private final IEmailSender emailSender;
    // DI interface üzerinden yapılırsa Dependecy Inversion Principle (DIP) 'e uygun olur.
    // Dependency Injection (DI) ile UserRepository bean'ini alıyoruz.
    // UserRepository userRepository -> Constructor injection ile UserService bean ayağa kaldırılırken aslında UserRepository bean'i de enjekte edilir. Servise bağımlılık olarak eklenmiş olur.

    // 1.Yöntem daha çok tercih edilen
//    public UserService(UserRepository userRepository, IEmailSender turkcellEmailSender) {
//        this.userRepository = userRepository;
//        this.emailSender = turkcellEmailSender;
//    }

     // @Qualifier anatasyon ile kullanımı
    public UserService(UserRepository userRepository, @Qualifier("sendGridEmailSender") IEmailSender sa) {
        this.userRepository = userRepository;
        this.emailSender = sa;
    }

    // aslında DI ile SpringContext üzerinden gönderilen isimi değiştirmek gerekir.

    public String getUserInfo() {
        log.info("getUserInfo");
        return userRepository.getUserByName("Alice");
    }

    public void save(){
        log.info("Save");
        emailSender.sendEmail("test","Test","Test Email");
    }
}


