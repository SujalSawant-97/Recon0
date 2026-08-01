package com.example.Recon0;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableCaching

@EnableJpaRepositories("com.example.Recon0.repositories")
public class Recon0Application {

	public static void main(String[] args) {
		SpringApplication.run(Recon0Application.class, args);
	}

}
