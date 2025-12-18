package com.tcell_spring.restservice;

import com.tcell_spring.restservice.domain.service.UserService;
import com.tcell_spring.restservice.domain.service.UserServiceImp;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
public class RestserviceApplication {


	public static void main(String[] args) {
		SpringApplication.run(RestserviceApplication.class, args);
//		runApplication(args);
	}

	public static void runApplication(String[] args) {

		ApplicationContext context = SpringApplication.run(RestserviceApplication.class, args);
		String myBean = context.getBean("myBean", String.class);
		log.info(myBean);
		// spring context bean retrieval
		User user  = context.getBean("userInfo", User.class);
		log.info(user.getName());

		// Stereotype.Service ile işaretlenmiş UserService bean'ini al ve kullan
		UserService userService = context.getBean(UserService.class);
		userService.save();


		// DI olmasaydı Spring Context bunu yönetmeseydi
//		UserRepository userRepository = new UserRepository();
//		TurkcellEmailSender turkcellEmailSender = new TurkcellEmailSender();
//		UserService userService2 = new UserService(userRepository, turkcellEmailSender);
//		userService2.getUserInfo();
//		log.info(userService2.getUserInfo());

		// Son Senaryo ise 1 adet interface üzerinden bu interfaceden implemente olan birden fazla class varsa nasıl bir spring context yönetimi olur ? @Qualifier ile ayırt edebiliriz. @Primary ile birini default yapabiliriz.


		// Senaryo:2
		UserServiceImp userServiceImp = context.getBean(UserServiceImp.class);
		userServiceImp.save("turkcellEmailSender");




		// Bir servis içerisindeki Dependecy kısmını DI ile nasıl alıcağız.
	}

	// @Bean annotation to define a bean, register spring container
	@Bean
	public String myBean() {
	    return "testing bean";
	}

	// Class instanceları proje başlatıldığında oluşturulur ve Spring konteynerine eklenir. Daha sonra bu bean'ler, uygulamanın herhangi bir yerinde ihtiyaç duyulduğunda Spring konteynerinden alınabilir ve kullanılabilir.
	// Bu, bağımlılık enjeksiyonu (dependency injection) olarak bilinen bir tasarım desenini destekler ve uygulamanın modülerliğini, test edilebilirliğini ve bakımını artırır.
	@Bean
	public User userInfo() {
		User user = new User();
		user.setName("John Doe");
		return user;
	}

	// bu beanlerin uygulamadaki yaşam döngüsü, kapsamı (scope) ve diğer özellikleri de @Bean anotasyonu ile yapılandırılabilir.
	// Örneğin, bir bean'in singleton (varsayılan) veya prototype scope'ta sahip olup olmayacağını belirleyebilirsiniz.
	public class User{

		@PostConstruct
		public void init() {
			log.info("User bean is created");
		}

		@PreDestroy // cleanup işlemleri için kullanılır
		public void destroy() {
			log.info("User bean is destroyed");
		}

		private String name;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}

}
