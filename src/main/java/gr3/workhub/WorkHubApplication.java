package gr3.workhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableAsync
public class WorkHubApplication {

    public static void main(String[] args) {
        // Set default timezone to GMT+7
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("GMT+7"));
        SpringApplication.run(WorkHubApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}