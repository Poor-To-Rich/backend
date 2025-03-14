package com.poortorich.email.config;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class emailConfig {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private Integer port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private Boolean auth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private Boolean startTlsEnable;

    @Value("${spring.mail.properties.mail.smtp.starttls.required}")
    private Boolean startTlsRequired;

    @Value("${spring.mail.properties.mail.smtp.connectiontimeout}")
    private Integer connectionTimeout;

    @Value("${spring.mail.properties.mail.smtp.timeout}")
    private Integer timeout;

    @Value("${spring.mail.properties.mail.smtp.writetimeout}")
    private Integer writeTimeout;

    private Properties getMailProperties() {
        Properties properties = new Properties();

        properties.put("mail.smtp.auth", auth);
        properties.put("mail.smtp.starttls.enable", startTlsEnable);
        properties.put("mail.smtp.starttls.required", startTlsRequired);
        properties.put("mail.smtp.connectiontimeout", connectionTimeout);
        properties.put("mail.smtp.timeout", timeout);
        properties.put("mail.smtp.wrtietimeout", writeTimeout);

        return properties;
    }

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setDefaultEncoding("UTF-8");
        mailSender.setJavaMailProperties(getMailProperties());

        return mailSender;
    }
}
