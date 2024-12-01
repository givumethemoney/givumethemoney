package com.hey.givumethemoney;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableJpaRepositories(basePackages = "com.hey.givumethemoney.repository")
@EntityScan(basePackages = "com.hey.givumethemoney.domain")
public class GivumethemoneyApplication {

	public static void main(String[] args) {
		SpringApplication.run(GivumethemoneyApplication.class, args);
	}
}
