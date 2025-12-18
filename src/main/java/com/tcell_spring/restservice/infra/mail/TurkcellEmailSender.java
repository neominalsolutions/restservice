package com.tcell_spring.restservice.infra.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// 50 references

@Component
@Slf4j
public class TurkcellEmailSender implements IEmailSender {
    @Override
    public void sendEmail(String to, String subject, String body) {
      log.info("Sending email to From Turkcell" + to);
    }
}

// UserService Dependecy olarak TurkcellEmailService bağımlı olsun istemeyiz. Bunun yerine IEmailSender interface'ine bağımlı olsun isteriz.
// Bu sayede ileride Email gönderim provider'ını değiştirmek istediğimizde UserService'i değiştirmemize gerek kalmaz. Sadece konfigürasyon değişikliği yapmamız yeterli olur.
// Solid prensiplerine de uygun bir tasarım olur.
// Dependecy Inversion Principle (DIP) 'e uygun bir tasarım olur.