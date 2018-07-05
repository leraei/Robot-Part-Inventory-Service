package com.kahoot.robotpartservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Service Application.
 */
@SpringBootApplication
public class RobotPartServiceApplication {

	public static void main(final String[] args) {
		final SpringApplication springApplication = new SpringApplication(RobotPartServiceApplication.class);
		springApplication.run(args);
	}
}
