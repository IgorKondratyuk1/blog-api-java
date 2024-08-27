package org.development.blogApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BlogApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogApiApplication.class, args);
    }

//    @Autowired
//    private EmailService emailService;
//
//    @Bean
//    public CommandLineRunner conditionalRunner() {
//        return args -> {
//            System.out.println("Conditional CommandLineRunner running!");
//            emailService.send("", "");
//        };
//    }

}
