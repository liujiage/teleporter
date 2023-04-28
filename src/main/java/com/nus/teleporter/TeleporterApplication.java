package com.nus.teleporter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


@SpringBootApplication(scanBasePackages = "com.nus.teleporter")
public class TeleporterApplication extends SpringBootServletInitializer {

	/*****
	 * Start teleporter demo
	 */
	public static void main(String[] args) {
		SpringApplication.run(TeleporterApplication.class);
	}

}
