package com.example.be_datn;

import com.cloudinary.Cloudinary;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableAsync
public class BeDatnApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeDatnApplication.class, args);
    }
}
