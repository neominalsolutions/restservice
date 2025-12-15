package com.tcell_spring.restservice.infra;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// 100 references
// UserService(SendGridEmailSender) --> IEmailSender --> SendGridEmailSender --> Yanlış bağımlılık yönü
// UserService(IEmailSender) --> SendGridEmailSender --> Doğru bağımlılık yönü

@Component
@Slf4j
public class SendGridEmailSender implements IEmailSender{
    @Override
    public void sendEmail(String to, String subject, String body) {
      log.info("Sending email to From Sendgrid" + to);
    }
}


// SendGridEmailSender -> bean name sendGridEmailSender