package com.poortorich;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {
        "com.poortorich.category.entity",
        "com.poortorich.user.entity",
        "com.poortorich.expense.entity",
        "com.poortorich.income.entity",
        "com.poortorich.iteration.entity",
        "com.poortorich.chat.entity",
        "com.poortorich.like.entity",
        "com.poortorich.tag.entity"
})
public class PoorToRichApplication {

    public static void main(String[] args) {
        SpringApplication.run(PoorToRichApplication.class, args);
    }
}
