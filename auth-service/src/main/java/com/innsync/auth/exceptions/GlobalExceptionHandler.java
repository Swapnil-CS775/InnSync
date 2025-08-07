package com.innsync.auth.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.innsync.auth.dto.ErrorResponseDto;

//This annotation tells Spring that this class is a global advice component
//for all @RestController classes.
@RestControllerAdvice
public class GlobalExceptionHandler {
	
	// This annotation marks this method as the handler for one specific exception type:
    // EmailAlreadyExistsException.class. If any other exception is thrown, this method is ignored.
	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ResponseEntity<ErrorResponseDto> handleEmailAlreadyExistsException(
			EmailAlreadyExistsException exception, // The actual exception object that was caught.
			WebRequest webRequest  // An object injected by Spring holding details about the request.
			){
		
		// Here, we create a new instance of our custom error response DTO.
		ErrorResponseDto errorResponse=new ErrorResponseDto(

				LocalDateTime.now(), // Sets the current time.
				exception.getMessage(), // Gets the message from the exception ("Email is already registered...").
				webRequest.getDescription(false),// Gets the API path
				HttpStatus.CONFLICT.value() // Gets the integer value of the 409 status.
				);
		
		// We return a ResponseEntity. This gives us full control over the HTTP response.
        // We place our 'errorResponse' object in the body and set the HTTP status to 409 CONFLICT.
		return new ResponseEntity<ErrorResponseDto>(errorResponse,HttpStatus.CONFLICT);
	}
	
}

/*
 * File 3: GlobalExceptionHandler.java (The Central Handler)
Overall Purpose: This is the most important file. It acts as a central 
"control tower" that constantly listens for exceptions thrown from any controller.
 When it "catches" an exception it knows how to handle, it stops the default Spring
  error process and instead builds and returns our clean ErrorResponseDto.
 * */


/**
 * 
 * Summary of the Flow
1) Your AuthService throws a specific EmailAlreadyExistsException.

2) The GlobalExceptionHandler, because of @RestControllerAdvice, intercepts this exception.

3) It finds the @ExceptionHandler method that matches the specific exception type.

4) That method runs, builds a clean ErrorResponseDto, and sends it back to the frontend 
as a structured JSON response with the correct 409 Conflict HTTP status.
 */
