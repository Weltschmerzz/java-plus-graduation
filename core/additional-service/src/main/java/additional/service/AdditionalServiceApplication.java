package additional.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "ru.practicum.ewm.category",
        "ru.practicum.ewm.locations",
        "ru.practicum.ewm.exception"
})
@EntityScan(basePackages = {
        "ru.practicum.ewm.category.model",
        "ru.practicum.ewm.locations.model"
})
@EnableJpaRepositories(basePackages = {
        "ru.practicum.ewm.category.repository",
        "ru.practicum.ewm.locations.repository"
})
public class AdditionalServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdditionalServiceApplication.class, args);
    }
}
