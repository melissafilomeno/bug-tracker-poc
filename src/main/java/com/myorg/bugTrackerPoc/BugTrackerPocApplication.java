package com.myorg.bugTrackerPoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.myorg.bugTrackerPoc.*")
public class BugTrackerPocApplication {

	public static void main(String[] args) {
		SpringApplication.run(BugTrackerPocApplication.class, args);
	}

}
