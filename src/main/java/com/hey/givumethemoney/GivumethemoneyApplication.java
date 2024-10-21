package com.hey.givumethemoney;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class GivumethemoneyApplication {

	public static void main(String[] args) {
		SpringApplication.run(GivumethemoneyApplication.class, args);
	}
}
