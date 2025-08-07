package com.innsync.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {
	private String username; // This will hold either the email or phone
    private String password;
}
