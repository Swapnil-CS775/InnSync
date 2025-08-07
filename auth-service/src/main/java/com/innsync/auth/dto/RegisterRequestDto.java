package com.innsync.auth.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDto {
	@NotNull
    @Valid // This annotation triggers validation on the nested object
    private OwnerDto owner;

    @NotNull
    @Valid
    private BusinessDto business;
}

/*
 * Some notes - 
 * Note: The Purpose of DTOs (Data Transfer Objects)
DTOs are a critical design pattern in modern backend development. Their primary purposes are:

API Contract & Decoupling:

A DTO defines the "shape" of the data for your public API. It is a contract between your frontend and backend.

An @Entity defines the "shape" of your internal database table.

By using DTOs, you decouple your API contract from your database schema. This is essential because you can now
change your internal database structure (e.g., add or rename a column in your @Entity) without breaking the
API for your clients.

Security:

@Entity objects often contain sensitive data (like hashed passwords) or internal-only fields that should 
never be exposed to the outside world.

DTOs act as a security layer, allowing you to selectively expose only the necessary and safe fields to the 
client in API responses.

Data Validation:

DTOs are the correct and standard place to perform validation on incoming data.

By using validation annotations (@NotBlank, @Email, @Size, etc.) on DTO fields, you ensure that all data 
is sanitized and checked for correctness at the entry point of your application, before it ever reaches 
your business logic or database.

In summary: Think of an @Entity as the raw ingredients and internal structure of your kitchen. A DTO is 
the final, well-presented dish you serve to the customer—it contains exactly what they need to see, and 
nothing more.
 * */