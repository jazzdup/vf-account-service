package com.vodafone.charging.accountservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * Spring Boot Application class
 */
@ComponentScan(basePackages = "com.vodafone.charging")
@SpringBootApplication
@Slf4j
public class AccountServiceApplication {

    public static void main(String[] args) {

        ApplicationContext applicationContext = SpringApplication.run(AccountServiceApplication.class, args);
//        for (String name : applicationContext.getBeanDefinitionNames()) {
//            log.debug(name);
//        }

    }
}
