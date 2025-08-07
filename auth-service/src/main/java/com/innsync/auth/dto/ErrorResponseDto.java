package com.innsync.auth.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponseDto {
	// Field to hold the exact date and time the error occurred.
	private LocalDateTime timestamp;
	// Field for the primary, user-friendly error message.
    private String message;
    // Field for additional context, like the API path where the error happened.
    private String details;
    // Field to hold the integer value of the HTTP status code (e.g., 409).
    private int status;
}

/*
 * 
 * File 2: ErrorResponseDto.java (The Structured JSON Response)
Overall Purpose: This file defines a consistent, predictable JSON structure for all 
error messages sent back to the frontend. This ensures the frontend developer always 
knows what format to expect when an error occurs, making their job much easier.

*/
