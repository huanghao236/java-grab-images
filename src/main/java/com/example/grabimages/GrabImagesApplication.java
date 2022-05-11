package com.example.grabimages;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class GrabImagesApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrabImagesApplication.class, args);
    }

}
