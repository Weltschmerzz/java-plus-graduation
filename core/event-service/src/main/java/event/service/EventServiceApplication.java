package event.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "ru.practicum.ewm.events",
        "ru.practicum.ewm.exception",
        "ru.practicum.stats.client"
})
@EntityScan(basePackages = "ru.practicum.ewm.events.model")
@EnableJpaRepositories(basePackages = "ru.practicum.ewm.events.repository")
@EnableFeignClients(basePackages = "ru.practicum.ewm.events.client")
public class EventServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventServiceApplication.class, args);
    }
}
