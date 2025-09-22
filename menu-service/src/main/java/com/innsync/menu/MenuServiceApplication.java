package com.innsync.menu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MenuServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MenuServiceApplication.class, args);
	}

}


/*
 * That line tells Spring Boot: "Do NOT automatically create a default database connection for me. I will do it myself."

Concept: Spring Boot Auto-Configuration 🪄
One of Spring Boot's most powerful features is auto-configuration. When it sees certain libraries in your project 
(like the JDBC starter), it automatically tries to configure the necessary components for you.

Specifically, a class called DataSourceAutoConfiguration runs by default. Its job is to read the spring.datasource.* 
properties and create one single, default database connection (DataSource) for the entire application.

The Problem in Our Case
This automatic behavior is perfect for simple applications with only one database. However, our menu-service is more 
complex; it needs to manage multiple database connections.

If we let DataSourceAutoConfiguration run, it would conflict with the custom, multi-database setup we are creating in 
our DataSourceConfig.java class.

The Solution
The exclude = {DataSourceAutoConfiguration.class} property on the @SpringBootApplication annotation is how we solve this. 
It explicitly tells Spring Boot:

"Turn off your default, automatic database configuration. I am taking full manual control."

This prevents any conflicts and allows our custom DataSourceConfig to be the sole authority on how database connections are 
created and managed in the application.
 * 
 */