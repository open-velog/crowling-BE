package com.example.finding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing  //입력된 시간, 수정된 시간 같이 기록될 수 있도록 해줌
public class FindingApplication {

    public static void main(String[] args) {
        SpringApplication.run(FindingApplication.class, args);
    }

}
