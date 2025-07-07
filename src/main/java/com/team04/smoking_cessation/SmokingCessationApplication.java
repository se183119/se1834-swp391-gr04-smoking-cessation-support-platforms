package com.team04.smoking_cessation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmokingCessationApplication {

	public static void main(String[] args) {

		SpringApplication.run(SmokingCessationApplication.class, args);
		System.out.println("===============================================");
		System.out.println();
		System.out.println("http://localhost:8080/api/swagger-ui/index.html");
		System.out.println();
		System.out.println("===============================================");
	}

}
