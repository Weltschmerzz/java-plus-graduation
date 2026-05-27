package user.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "ru.practicum.ewm.users",
        "ru.practicum.ewm.exception"
})
@EntityScan(basePackages = "ru.practicum.ewm.users.model")
@EnableJpaRepositories(basePackages = "ru.practicum.ewm.users.repository")
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
