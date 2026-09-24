package com.example.pruebaThymeleaf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class PruebaThymeleafApplication {

	public static void main(String[] args) {
		SpringApplication.run(PruebaThymeleafApplication.class, args);
	}

}
