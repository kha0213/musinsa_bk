package com.yl.musinsa2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class Musinsa2Application {

    public static void main(String[] args) {
        SpringApplication.run(Musinsa2Application.class, args);
    }

}
