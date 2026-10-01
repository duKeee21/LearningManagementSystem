package com.university.learningmanagementsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LearningManagementSystemApplication {

	static void main(String[] args) {
		SpringApplication.run(LearningManagementSystemApplication.class, args);
	}

}
