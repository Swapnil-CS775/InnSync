package com.innsync.auth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//This annotation is a fallback. If our GlobalExceptionHandler did not exist,
//Spring would automatically respond with a 409 CONFLICT status because of this line.
@ResponseStatus(value = HttpStatus.CONFLICT)
public class EmailAlreadyExistsException extends RuntimeException {
	// This is the constructor for our custom exception.
	public EmailAlreadyExistsException(String message) {
		
		// 'super(message)' calls the constructor of the parent class (RuntimeException)
        // and passes the error message to it. This message is what we retrieve
        // later with exception.getMessage().
        super(message);
    }
}


/*
 * File 1: EmailAlreadyExistsException.java (The Custom Exception)
Overall Purpose: The goal of this file is to create our own specific,
 named error for a particular business rule violation (a duplicate email). 
 This is far better than using a generic RuntimeException because it makes 
 our code readable and allows us to "catch" this specific error in our handler.
 * *
 * 
 * 
 */