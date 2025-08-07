package com.innsync.auth.service;

import com.innsync.auth.dto.OwnerDto;
import com.innsync.auth.dto.RegisterRequestDto;

public interface AuthService {
	OwnerDto registerOwnerAndBusiness(RegisterRequestDto registerRequestDto);
	
	//We use void because this method's job is to perform an action (send an email), not return data.
	void processForgotPassword(String email);
	
	void resetPassword(String token, String newPassword);
}

/*
 *Concept: The End-to-End Data Flow
Here is the step-by-step journey of the data from the user's form submission to the final database record.

1. Presentation Layer (Controller)

A user fills out the registration form in the React frontend and clicks "Submit".

An HTTP POST request is sent to your backend API endpoint, for example, /api/auth/register. The body of this
 request is a JSON object.

Your AuthController receives the request. Spring automatically converts the incoming JSON into your 
RegisterRequestDto object.

Validation runs first: Spring checks the DTO against the validation annotations (@NotBlank, @Email, etc.).
 If invalid, it immediately sends back a 400 Bad Request error, and the process stops.

If valid, the Controller calls the authService.registerOwnerAndBusiness() method, passing the DTO. 
The controller's job is now done.

2. Business Logic Layer (AuthServiceImpl) - The Core Orchestration
This is where all the complex logic you asked about happens. The service orchestrates the entire process:

Mapping: The service receives the RegisterRequestDto. The first step is to map the data from the DTOs 
(OwnerDto, BusinessDto) into your JPA @Entity objects (Owner, Business).

Password Encryption: The service takes the plain-text password from the OwnerDto. It does not store it 
directly. Instead, it uses a PasswordEncoder bean to hash it into an unreadable format (e.g., $2a$10$...). 
This hashed string is what gets set on the Owner entity's password field.

Save Primary Records: The service calls ownerRepository.save(owner) and businessRepository.save(business). 
This saves the new owner and their business details to your main, central database (swapnil_registry). This 
action generates the unique id for the Business.

Dynamic DB Creation:

Now that the Business entity has an id, the service calls your DBUtility.generateIdentifier() method to create 
the unique database name (e.g., innsync_royal_cafe_101).

It then executes a raw SQL command (CREATE DATABASE innsync_royal_cafe_101;) to create this new database on 
the AlwaysData MySQL server.

Update Business Record: The service sets the new database name on the business.setDbIdentifier(...) field 
and calls businessRepository.save(business) again to persist this update.

Return Response: The service prepares a response, perhaps by mapping the saved Owner entity back to an 
OwnerDto, and returns it to the controller.

3. Response

The Controller receives the OwnerDto from the service and sends a 201 Created HTTP response back to the 
frontend, including the OwnerDto in the response body.

This flow ensures a clean separation of concerns, robust security through password hashing, and scalable 
multi-tenancy with dynamic database creation. 
 * */
