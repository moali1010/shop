package com.example.tacoshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TacoShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(TacoShopApplication.class, args);
    }

}
