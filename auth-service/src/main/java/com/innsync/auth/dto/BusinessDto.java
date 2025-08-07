package com.innsync.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessDto {
	
	 	@NotBlank(message = "Business name cannot be blank")
	    private String businessName;

	    @NotBlank(message = "Business type cannot be blank")
	    private String businessType; // e.g., CAFE, RESTAURANT

	    // Address fields
	    @NotBlank
	    private String addressLine1;

	    @NotBlank
	    private String city;

	    @NotBlank
	    private String state;

	    @NotBlank
	    private String pinCode;

	    // Optional field
	    private String gstNumber;
}
