package com.poortorich;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"com.poortorich.category.entity", "com.poortorich.user.entity", "com.poortorich.expense.entity"})
public class PoorToRichApplication {

    public static void main(String[] args) {
        SpringApplication.run(PoorToRichApplication.class, args);
    }
}
