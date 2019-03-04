package com.kibo.pegateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootConfiguration
@EnableAutoConfiguration
@SpringBootApplication
//@Profile("your-profile")
@ComponentScan(basePackages = {"com.kibo"})
public class PeGatewayApplication {
   public static void main(String[] args) {
        SpringApplication.run(PeGatewayApplication.class, args);
    }
}
