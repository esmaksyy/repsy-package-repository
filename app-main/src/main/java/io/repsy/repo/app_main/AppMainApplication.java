package io.repsy.repo.app_main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = "io.repsy.repo")
@EnableJpaRepositories(basePackages = "io.repsy.repo.repository")
@EntityScan(basePackages = "io.repsy.repo.entity")
public class AppMainApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppMainApplication.class, args);
	}

}
