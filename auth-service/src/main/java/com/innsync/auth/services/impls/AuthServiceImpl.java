package com.innsync.auth.services.impls;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.innsync.auth.dto.BusinessDto;
import com.innsync.auth.dto.OwnerDto;
import com.innsync.auth.dto.RegisterRequestDto;
import com.innsync.auth.entities.Address;
import com.innsync.auth.entities.Business;
import com.innsync.auth.entities.Owner;
import com.innsync.auth.entities.Role;
import com.innsync.auth.exceptions.EmailAlreadyExistsException;
import com.innsync.auth.repositories.BusinessRepository;
import com.innsync.auth.repositories.OwnerRepository;
import com.innsync.auth.service.AuthService;
import com.innsync.auth.service.EmailService;
import com.innsync.auth.util.DBUtility;

//Marks this class as a Spring Service component, making it eligible for dependency injection.
@Service
public class AuthServiceImpl implements AuthService{
	
    // Injects the OwnerRepository bean, allowing interaction with the 'owners' table.
	@Autowired
	private OwnerRepository ownerRepository;
	
    // Injects the BusinessRepository bean, for interacting with the 'businesses' table.
	@Autowired
	private BusinessRepository businessRepository;
	
    // Injects the PasswordEncoder bean (BCrypt) for securely hashing passwords.
	@Autowired 
	private PasswordEncoder passwordEncoder;
	
    // Injects the JdbcTemplate bean, used for executing raw SQL commands like CREATE DATABASE.
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
    private EmailService emailService;

    // Overrides the method from the AuthService interface, defining the registration logic.
	@Override
	public OwnerDto registerOwnerAndBusiness(RegisterRequestDto registerRequestDto) {
		
		// Step 1: Check if owner with the email already exists
        // Queries the database to find if an owner with the given email already exists.
		Optional<Owner> existingOwner=ownerRepository.findByEmail(registerRequestDto.getOwner().getEmail());
		
        // Checks if the 'Optional' container holds an Owner, meaning the email is already in use.
		if (existingOwner.isPresent()) {
            // We will create and throw a custom exception here later
            // Stops execution and throws an exception if the email is already registered.
            throw new EmailAlreadyExistsException("Email is already registered. Please log in.");
        }

        // TODO: Continue with mapping DTO to entities and saving them.
		// Step 2: Map OwnerDto to Owner Entity and Hash Password
        // Calls a private helper method to convert the OwnerDto into a database-ready Owner entity.
        Owner owner = mapToOwnerEntity(registerRequestDto.getOwner());

        // Step 3: Map BusinessDto to Business Entity
        // Calls a helper method to convert the BusinessDto into a Business entity.
        Business business = mapToBusinessEntity(registerRequestDto.getBusiness());

        // Step 4: Set the relationship
        // Establishes the foreign key relationship by linking the business to its owner.
        business.setOwner(owner);

        // Step 5: Save the entities
        // Saves the Owner entity to the database and gets the persisted object back (with its new ID).
        Owner savedOwner = ownerRepository.save(owner);
        Business savedBusiness = businessRepository.save(business);
		
        
        // TODO: Next, we will add the dynamic database creation logic here.
        // A try-catch block to handle potential errors during the critical database creation step.
        try {
            // Generates a unique database name using the business name and its newly created ID.
            String dbIdentifier = DBUtility.generateIdentifier(savedBusiness.getBusinessName(), savedBusiness.getId());
            // Executes a raw SQL command to create a new, dedicated database for this tenant.
            jdbcTemplate.execute("CREATE DATABASE `" + dbIdentifier + "`");

            // Update the business entity with the new dbIdentifier
            // Sets the generated database name on the business entity object.
            savedBusiness.setDbIdentifier(dbIdentifier);
            // Saves the business entity again; this time, it's an UPDATE to add the dbIdentifier.
            businessRepository.save(savedBusiness);

        } catch (Exception e) {
            // This is a compensating transaction. If DB creation fails, we should roll back the user creation.
            // For now, we'll throw a runtime exception. Proper handling would be more complex.
            ownerRepository.delete(savedOwner); // Clean up the created owner and business
            throw new RuntimeException("Could not create tenant database. Registration rolled back.", e);
        }
        
        // If successful, calls a helper method to convert the saved Owner entity into a safe DTO for the API response.
		return mapToOwnerDto(savedOwner);
	}
	
	// Helper method to map data from OwnerDto to an Owner entity.
	private Owner mapToOwnerEntity(OwnerDto ownerDto) {
		Owner owner=new Owner();
		owner.setFullName(ownerDto.getFullName());
		owner.setEmail(ownerDto.getEmail());
		owner.setPhone(ownerDto.getPhone());
		// Hashes the plain-text password before setting it on the entity for secure storage.
		owner.setPassword(passwordEncoder.encode(ownerDto.getPassword()));
		owner.setRole(Role.OWNER);
		
		return owner;
	}
	
    // Helper method to map data from BusinessDto to a Business entity.
	private Business mapToBusinessEntity(BusinessDto businessDto) {
		Business business=new Business();
		business.setBusinessName(businessDto.getBusinessName());
		business.setBusinessType(businessDto.getBusinessType());
		business.setGstNumber(businessDto.getGstNumber());
		
        // Creates and populates the embeddable Address object.
		Address address=new Address();
		address.setAddressLine1(businessDto.getAddressLine1());
		address.setCity(businessDto.getCity());
		address.setState(businessDto.getState());
		address.setPinCode(businessDto.getPinCode());
		
        // Sets the populated address on the business entity.
		business.setAddress(address);
		
		return business;
	}
	
    // Helper method to map an Owner entity to a safe OwnerDto for API responses.
	private OwnerDto mapToOwnerDto(Owner owner) {
		OwnerDto ownerDto=new OwnerDto();
		ownerDto.setFullName(owner.getFullName());
		ownerDto.setEmail(owner.getEmail());
		ownerDto.setPhone(owner.getPhone());
        // Note: The hashed password is intentionally NOT included in the DTO.
		ownerDto.setRole(owner.getRole().name());
		return ownerDto;
	}

	@Override
	public void processForgotPassword(String email) {
		// TODO Auto-generated method stub
		// Find the user by email.
        ownerRepository.findByEmail(email).ifPresent(owner -> {
            // If the user exists, proceed. If not, we do nothing to prevent user enumeration.

            // 1. Generate a secure, random token.
            String token = UUID.randomUUID().toString();

            // 2. Set the token and its expiration date on the user entity.
            owner.setPasswordResetToken(token);
            owner.setTokenExpiryDate(LocalDateTime.now().plusMinutes(15)); // Token is valid for 15 minutes

            // 3. Save the updated user record.
            ownerRepository.save(owner);

            // 4. Use the EmailService to send the reset link.
            emailService.sendPasswordResetEmail(owner.getEmail(), token);
        });
	}

	@Override
	public void resetPassword(String token, String newPassword) {
		// TODO Auto-generated method stub
		// 1. Find the user by the password reset token.
	    Owner owner = ownerRepository.findByPasswordResetToken(token)
	            .orElseThrow(() -> new RuntimeException("Invalid password reset token."));

	    // 2. Check if the token has expired.
	    if (owner.getTokenExpiryDate().isBefore(LocalDateTime.now())) {
	        // Invalidate the token even if it's expired
	        owner.setPasswordResetToken(null);
	        owner.setTokenExpiryDate(null);
	        ownerRepository.save(owner);
	        throw new RuntimeException("Password reset token has expired.");
	    }

	    // 3. Hash the new password and update the user's record.
	    owner.setPassword(passwordEncoder.encode(newPassword));

	    // 4. Invalidate the token by setting it and its expiry to null.
	    owner.setPasswordResetToken(null);
	    owner.setTokenExpiryDate(null);

	    // 5. Save the updated user.
	    ownerRepository.save(owner);
	}
	
	
}


/*
 * Concept: Why Manual Mapping is Necessary Here
You are correct. We are doing this mapping manually precisely because we are using DTOs and need to apply
 custom business logic.

If we were to directly accept an @Entity object from the API (a bad practice), the process would seem more 
"automatic," but we would lose control. Here’s why the service layer's manual mapping is essential:

Transformation & Decoupling: The DTO is the public contract; the Entity is the internal database structure. 
They serve different purposes and may have different fields. The service layer's job is to be the bridge,
 transforming the incoming public data into the internal storage format.

Applying Business Logic: The mapping process is where we inject crucial business logic that cannot be
 automated. In our case:

Password Hashing: We cannot just copy the password. We must intercept it and run it through passwordEncoder.
encode().

Setting Relationships: We need to explicitly create the link between the Owner and the Business by calling 
business.setOwner(owner).

Orchestration of Complex Tasks: This is where we will add the logic to create a new database for the tenant,
 which is a highly custom operation.

Security: This manual step ensures we only take the data we need from the DTO and build our entities securely,
 rather than letting a framework automatically bind potentially malicious data to our internal models.

In short: You are performing this "manual" mapping because you are not building a simple data-in, data-out 
application. You are building an application with specific business rules, and the service layer is where
 those rules are enforced.


 * */


/*
 * 
 * 
private Owner mapToOwnerEntity(OwnerDto ownerDto) --> this is also for mapping Owner with OwnerDto

private Business mapToBusinessEntity(BusinessDto businessDto)

Purpose: This method's job is to take the incoming data from the request (BusinessDto) and transform 
it into an object that can be saved to the database.

Requirement: The businessRepository.save() method requires an @Entity object as its parameter.

Conclusion: Therefore, the return type must be Business (the entity).


private OwnerDto mapToOwnerDto(Owner owner)

Purpose: This method's job is to take the data that was just saved in the database (Owner entity) and 
prepare it to be sent back to the frontend as a safe and clean API response.

Requirement: As we discussed, we must never expose our internal @Entity objects in a public API response 
for security and decoupling reasons.

Conclusion: Therefore, the return type must be OwnerDto (the data transfer object), which only contains 
the safe fields to be shown to the user.
 * */


/*
 * Concept: What is JdbcTemplate?
JdbcTemplate is a central class in the Spring Framework that simplifies working with raw JDBC
 (Java Database Connectivity).

The Problem it Solves:
Standard JDBC code is verbose and prone to errors. You have to manually manage database connections,
 statements, and result sets, and you must always remember to close them in finally blocks to prevent 
 resource leaks.

The JdbcTemplate Solution:
JdbcTemplate handles all of this boilerplate for you. It takes care of:

Opening and closing database connections.

Executing statements and handling exceptions.

Translating low-level SQLException into a more informative Spring exception hierarchy.

You simply provide the SQL you want to run and process the results.

Why We Need It:
JPA and Hibernate are designed for DML (Data Manipulation Language) like SELECT, INSERT, UPDATE on 
existing tables. They are not designed to execute DDL (Data Definition Language) commands 
like CREATE DATABASE. For that specific, low-level task, JdbcTemplate is the perfect tool.
 * */


/*
 * Tenant Onboarding (Owner Registration):

This is what we just built. It's the public-facing registration for a new business owner.

Because only an owner can register a new business on the platform, we automatically and correctly assign 
them the OWNER role. Our line owner.setRole(Role.OWNER); achieves this perfectly.

Staff Onboarding (Future Feature):

This will be a completely different feature, accessible only to a logged-in owner or manager.

In that workflow, the owner will fill out a form to add a new staff member, and they will explicitly 
choose a role like MANAGER or STAFF from a dropdown menu.
 * */
