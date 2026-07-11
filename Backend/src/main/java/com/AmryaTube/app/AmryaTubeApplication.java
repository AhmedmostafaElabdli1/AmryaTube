package com.AmryaTube.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class AmryaTubeApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmryaTubeApplication.class, args);
	}

}
