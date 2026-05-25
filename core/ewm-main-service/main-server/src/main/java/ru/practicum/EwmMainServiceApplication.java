package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "ru.practicum")
@EnableFeignClients(basePackages = {
        "ru.practicum.ewm.users.client",
        "ru.practicum.ewm.category.client"
})
public class EwmMainServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EwmMainServiceApplication.class, args);
    }
}
