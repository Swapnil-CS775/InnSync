package com.innsync.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.innsync.auth.security.JwtAuthEntryPoint;
import com.innsync.auth.security.JwtAuthFilter;
import com.innsync.auth.security.OwnerUserDetailsService;

@Configuration
public class SecurityConfig {
	
	@Autowired
	private JwtAuthEntryPoint jwtAuthEntryPoint;
	
	@Autowired
	private OwnerUserDetailsService ownerUserDetailsService;
	
	@Autowired
	private JwtAuthFilter jwtAuthFilter;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // 1. Disable CSRF, as it's not needed for stateless REST APIs
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthEntryPoint))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 2. Define authorization rules for HTTP requests
            .authorizeHttpRequests(auth -> auth
                // 3. Make the registration endpoint public for everyone
                .requestMatchers("/api/auth/register",
                		"/api/auth/login",
                		"/api/auth/forgot-password", 
                		"/api/auth/reset-password").permitAll()
                // 4. Any other request that hasn't been matched yet must be authenticated
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            // 2. Add your filter to the chain before the standard username/password filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // Build and return the configured security filter chain
        return http.build();
    }
	
	@Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(ownerUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}


/*
 * Notes: Spring @Configuration and @Bean
1. @Configuration

What it is: A class-level annotation.

Purpose: It marks a class as a source of bean definitions. The Spring IoC (Inversion of Control) container 
processes this class to generate beans that it will manage.

2. @Bean

What it is: A method-level annotation.

Purpose: It is used within a @Configuration class to declare a bean. The method's name becomes the bean's 
ID by default. The object returned by this method is registered as a bean in the Spring Application Context. 
Spring manages the entire lifecycle of this object, and by default, it is a singleton (only one instance is 
created for the entire application).

3. PasswordEncoder

What it is: A Spring Security interface. It's a contract, not an implementation.

Purpose: It standardizes the process of encoding (hashing) passwords. It provides two core methods:

encode(rawPassword): Hashes a plain-text password.

matches(rawPassword, encodedPassword): Compares a plain-text password against a stored hash to see if 
they match.

Benefit: Your code depends on this stable interface, allowing the underlying hashing algorithm to be changed 
easily without altering your business logic.

4. BCryptPasswordEncoder

What it is: A concrete implementation of the PasswordEncoder interface.

Purpose: It uses the industry-standard BCrypt strong hashing algorithm.

Key Feature: BCrypt automatically incorporates a randomly generated salt into each hash. This ensures that 
even if two users have the same password, their stored hashes will be completely different, which is a 
critical defense against dictionary and rainbow table attacks.

In Summary: Our SecurityConfig class tells Spring to execute the passwordEncoder() method. Spring then 
creates a single instance of BCryptPasswordEncoder and registers it as a managed bean of type PasswordEncoder. 
Now, any other component, like our AuthServiceImpl, can receive this exact instance via dependency injection 
to securely handle passwords.
 */





/*
 * Concept: The "Secure by Default" Philosophy
The behavior you are seeing is an intentional and critical design choice by the Spring Security team called
 "Secure by Default".

The philosophy is this: It is far safer to start with your entire application locked down and explicitly 
open up the few paths that need to be public (like /register or /login), rather than starting with 
everything open and trying to remember to secure every sensitive endpoint.

Forgetting to make a registration page public is a minor bug.

Forgetting to secure an admin page is a major security vulnerability.

So, you are correct that we don't want to keep it "as it is." But we also don't want to disable it.
The professional approach is to configure it to tell Spring Security exactly which routes are public 
and which are private.

We do this by defining a SecurityFilterChain bean.



Explanation of the Configuration
.csrf(csrf -> csrf.disable()): Disables Cross-Site Request Forgery protection. This is standard practice 
for stateless REST APIs that are consumed by clients like React, as token-based authentication (which we 
will add later) provides protection against this.

.requestMatchers("/api/auth/register").permitAll(): This is the most important line for your current 
problem. It tells Spring Security to find any request matching the path /api/auth/register and to 
permit all traffic to it, without requiring any authentication.

.anyRequest().authenticated(): This is the "catch-all" rule. It says that for any other request 
that does not match the rules above, the user must be authenticated. This maintains the secure-by-default 
posture.

With this configuration, your /api/auth/register endpoint is now public and can be accessed without 
credentials, while all other future endpoints you create will remain protected by default. This is the 
correct, professional way to manage security.

 * */


/*
 * Breakdown of the Two Changes
Here is a focused look at exactly what was added and why.

1. Injecting the JwtAuthFilter
We first needed to get a reference to the JwtAuthFilter bean that Spring created for you.

What we added:

Java

    @Autowired
    private JwtAuthFilter jwtAuthFilter; // We added this line
    
Why: This line uses @Autowired to tell Spring, "Please find the JwtAuthFilter bean and inject it here." 
Now, our SecurityConfig class has access to your filter and can tell the security chain to use it.

2. Adding the Filter to the Security Chain
Next, we had to officially place your filter into Spring Security's process.

What we added (inside the securityFilterChain method):

Java

            // This is the new line we added to the chain
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
            
Why: This is the most important step. Let's break down this single line:

.addFilterBefore(...): This is a command that tells Spring Security to insert a custom filter into its 
chain of security checks.

jwtAuthFilter: This is the first argument. We are telling it which filter to add (the one we just injected).

UsernamePasswordAuthenticationFilter.class: This is the second argument. It tells Spring Security where 
to place our filter in the chain. This line means: "Place my jwtAuthFilter before the standard filter 
that handles form-based username and password logins."

We do this because we want our application to check for a valid JWT first. If a valid token is present, 
our filter authenticates the user, and the process is done.

So, in summary, we simply gave our SecurityConfig access to your new filter and then officially placed 
it in the line of duty, ensuring it checks for a JWT on every incoming request before any other 
authentication happens.

 * */



/*
 * Concept: The User Enumeration Vulnerability 🔓
Providing specific feedback on whether the username or the password was incorrect allows 
attackers to perform a User Enumeration (or Username Enumeration) attack.

Here's how an attacker would exploit it:

Attempt 1: The attacker tries to log in with random_email@example.com and a random password.

Your Response: "User not found."

Attacker's Knowledge: The attacker now knows that random_email@example.com is not a valid, 
registered email in your system.

Attempt 2: The attacker tries to log in with rohan.d@example.com (an email they want to verify) 
and a random password.

Your Response: "Incorrect password."

Attacker's Knowledge: The attacker now has 100% confirmation that rohan.d@example.com is a valid, 
registered user.

Once an attacker has a list of valid user emails, they can target those users with more focused attacks 
like password spraying (trying common passwords), phishing, or social engineering.

The Secure Best Practice
The industry standard is to always return a generic and ambiguous error message for any type of login 
failure. Messages like:

"Invalid username or password."

"Invalid credentials."

"The login information you provided is incorrect."

This gives the attacker zero information about whether they guessed the username correctly or not, 
completely shutting down user enumeration attempts.


 * */
