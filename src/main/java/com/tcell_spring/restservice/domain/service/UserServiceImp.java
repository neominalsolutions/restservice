package com.tcell_spring.restservice.domain.service;

import com.tcell_spring.restservice.infra.mail.IEmailSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImp {

    private final ApplicationContext context;

    //  @Value("${defaultEmailProvider}") String emailProviderBeanName -> Config dosyası üzerinden default email provider ismini alıyoruz.

//    @Autowired -> Bunu artık kullanmalım. Constructor injection kullanıyoruz.
    private final String emailProviderBeanName;

    public UserServiceImp(ApplicationContext context,   @Value("${defaultEmailProvider}") String emailProviderBeanName) {
        this.emailProviderBeanName = emailProviderBeanName;
        this.context = context;
    }

    public void save(String providerBeanName){
        log.info("save v2");
        // Service Locator Pattern
        IEmailSender sender = context.getBean(emailProviderBeanName,IEmailSender.class);
        sender.sendEmail("","","");
    }
}
