package com.tcell_spring.restservice.infra;

// Senaryo Email gönderim işlemi var fakat burada Email farklı providerlar ile gönderilebilir.
// Örneğin SendGrid, Turkcell Email Service gibi farklı providerlar olabilir.
// Bu yüzden bir interface tanımlıyoruz ki farklı providerlar bu interface'i implemente edebilsinler.


import org.springframework.stereotype.Component;

@Component
public interface IEmailSender {
    void sendEmail(String to, String subject, String body);
}
