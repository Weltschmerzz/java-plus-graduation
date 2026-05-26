package request.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "ru.practicum.ewm.requests",
        "ru.practicum.ewm.exception"
})
@EntityScan(basePackages = "ru.practicum.ewm.requests.model")
@EnableJpaRepositories(basePackages = "ru.practicum.ewm.requests.repository")
@EnableFeignClients(basePackages = "ru.practicum.ewm.requests.client")
public class RequestServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RequestServiceApplication.class, args);
    }
}
