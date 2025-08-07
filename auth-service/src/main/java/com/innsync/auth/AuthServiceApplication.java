package com.innsync.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

}


/*
 *Concept: Spring Boot's Component Scanning
By default, when you run your AuthServiceApplication.java, the @SpringBootApplication annotation 
tells Spring to scan for components (@Service, @Repository, @Controller, etc.) in two places:

The package where the main application class is located (com.innsync.auth).

All of its sub-packages (e.g., com.innsync.auth.some.other.package). 
 * */
 