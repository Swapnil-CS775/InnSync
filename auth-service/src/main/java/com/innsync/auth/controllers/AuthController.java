package com.innsync.auth.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.innsync.auth.dto.ForgotPasswordRequestDto;
import com.innsync.auth.dto.LoginRequestDto;
import com.innsync.auth.dto.LoginResponseDto;
import com.innsync.auth.dto.OwnerDto;
import com.innsync.auth.dto.RegisterRequestDto;
import com.innsync.auth.dto.ResetPasswordRequestDto;
import com.innsync.auth.service.AuthService;
import com.innsync.auth.util.JwtUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	// Inject the service interface
	@Autowired
	private AuthService authService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	
	@PostMapping("/register")
	public ResponseEntity<OwnerDto> registerOwner(@Valid @RequestBody RegisterRequestDto registerRequestDto){
		// Delegate the business logic to the service layer
		OwnerDto createOwner=authService.registerOwnerAndBusiness(registerRequestDto);
		
		// Return the created owner DTO with a 201 Created HTTP status
		return new ResponseEntity<>(createOwner,HttpStatus.CREATED);
	}
	
	// Add this new login endpoint
	@PostMapping("/login")
	public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto){
		
        // 1. Let Spring Security authenticate the user
		Authentication authentication=authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword())
				);
		
		// 2. If authentication is successful, generate a JWT
		if(authentication.isAuthenticated()) {
			UserDetails userDetails=(UserDetails) authentication.getPrincipal();
			//authentication.getPrincipal(): The "principal" is the identity of the user who just logged in.
			//It's the "who" of the authentication.
			String token = jwtUtil.generateToken(userDetails);
			return ResponseEntity.ok(new LoginResponseDto(token));
		}else {
			// This part is for safety; the manager usually throws an exception on failure
            throw new UsernameNotFoundException("Invalid user request!");
		}
		
	}
	
	@PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto requestDto) {
        authService.processForgotPassword(requestDto.getEmail());
        // Always return a generic success message to prevent user enumeration.
        return ResponseEntity.ok("If an account with that email exists, a password reset link has been sent.");
    }
	
	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequestDto requestDto) {
	    authService.resetPassword(requestDto.getToken(), requestDto.getNewPassword());
	    return ResponseEntity.ok("Password has been reset successfully.");
	}
	
}



/*
 * Concept 1: The REST Controller Layer
The Controller is the entry point for your application. Its only responsibility is to handle incoming HTTP 
requests, delegate the actual work to the Service Layer, and then format and return an HTTP response. 
It acts as the bridge between the web (HTTP) and your Java business logic.

We will build a REST (Representational State Transfer) controller. This is an architectural style where 
we use standard HTTP methods (POST, GET, PUT, DELETE) to interact with resources. To create a new 
resource (like an owner/business), the standard method is POST.

Concept 2: Key Spring Annotations
@RestController: A class-level annotation that marks this class as a request handler. It tells Spring 
that the return value of methods should be automatically converted to JSON and written into the HTTP 
response body.

@RequestMapping("/api/auth"): A class-level annotation that defines a base URL path for all endpoints 
in this controller. All our auth-related endpoints will start with /api/auth.

@PostMapping("/register"): A method-level annotation that maps HTTP POST requests for the path /register 
to this specific method. The final URL will be /api/auth/register.

@RequestBody: A parameter-level annotation. It tells Spring to take the JSON body of the incoming POST 
request and convert it into our RegisterRequestDto Java object.

@Valid: A parameter-level annotation. When used with @RequestBody, it triggers the validation rules 
(@NotBlank, @Email, etc.) that we defined on our DTOs. If validation fails, Spring automatically 
returns a 400 Bad Request error.

 * */
