package com.innsync.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OwnerDto {
	@NotBlank(message = "Full name cannot be blank")
	private String fullName;

	@NotBlank(message = "Email cannot be blank")
	@Email(message = "Invalid email format")
	private String email;
	
	@NotBlank(message = "Phone number cannot be blank")
	@Size(min =10,max = 10,message = "Phone number must be 10 digits")
	private String phone;
	
	@NotBlank(message = "Password cannot be blank")
	@Size(min = 8, message = "Password must be at least 8 characters long")
	private String password;

	private String role;
}


/*
 * Concept: Context-Specific DTOs
This highlights an important design principle. The data needed for a request can be different from the 
data needed for a response.

Registration Request: We don't need a role field in the request DTO because the user doesn't provide their 
own role. Our backend automatically assigns it as OWNER.

Registration Response: We do want to include the role field in the response DTO. This tells the frontend 
client (e.g., the React app) that the newly registered user is an OWNER, so it can, for example, redirect 
them to the admin dashboard.

For simplicity, we are using the same OwnerDto for both, so we will add the role field to it.



Concept: Input vs. Output Fields in a DTO
A single DTO class like OwnerDto can be used for both incoming requests and outgoing responses. However, 
not every field is relevant for both directions.

Input Data (The Request)
This is the data your frontend sends to the backend when the user fills out the registration form.

The user provides their fullName, email, and password.

We must validate this incoming data with annotations like @NotBlank and @Email to ensure it's correct 
before we process it.

The user does not send a role. The system assigns the role automatically.

Output Data (The Response)
This is the data your backend sends back to the frontend after successfully creating the owner.

We send back the fullName, email, and the newly assigned role. (We never send the password back).

Since our own backend code is generating this output data, we don't need to validate it. We already trust 
it's correct. The validation annotations only apply to incoming data.

Conclusion
So, when I said the role is an "output-only field in this context," it means:

For the registration request: The role field is ignored. The client doesn't send it, so there's nothing to 
validate.

For the registration response: Our backend creates and includes the role field, so the frontend knows the 
new user is an OWNER.

This is why we added the private String role; field to the OwnerDto but did not add any validation 
annotations to it.
 * */
