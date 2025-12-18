package com.tcell_spring.restservice.infra.mail;

import org.springframework.stereotype.Component;

// Bad practices: Tek bir sınıfın birden fazla sorumluluğu var. (Single Responsibility Principle ihlali), OCP ihlali
// Mail gönderim işlemi farklı providerlar ile yapılabilir. Bu yüzden her provider için ayrı bir sınıf oluşturulmalı ve ortak bir interface implemente edilmeli.
// Bu sayede yeni bir provider eklemek istediğimizde mevcut sınıfları değiştirmemize gerek kalmaz, sadece yeni bir sınıf ekleriz.
@Component
public class MailSender {

    public void sendTurkcellEmail(String to, String subject, String body) {
        // Turkcell Email gönderim kodları burada olacak
    }

    public void sendSendGridEmail(String to, String subject, String body) {
        // SendGrid Email gönderim kodları burada olacak
    }
}
