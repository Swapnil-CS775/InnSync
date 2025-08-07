package com.innsync.auth.entities;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "owners",
		uniqueConstraints = @UniqueConstraint(columnNames = {"email","phone"})
		)
public class Owner implements UserDetails{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String fullName;
	
	@Column(nullable = false)
	private String email;
	
	@Column(nullable = false)
	private String phone;
	
	@Column(nullable = false)
	private String password;
	
	@Column(name = "createdAt", nullable = false, updatable = false)
	private LocalDateTime createdAt;
	@PrePersist
	protected void onCreate() {
		this.createdAt=LocalDateTime.now();
	}
	
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;
	
	@Column(name = "password_reset_token")
	private String passwordResetToken;

	@Column(name = "token_expiry_date")
	private LocalDateTime tokenExpiryDate;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// Returns a list containing the user's role (e.g., "OWNER")
        return List.of(new SimpleGrantedAuthority(role.name()));
	}


	@Override
	public String getUsername() {
		// Spring Security's "username" is our application's "email"
		return this.email;
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	@Override
    public boolean isAccountNonLocked() {
        return true; // Or add logic for account locking
    }
	
	@Override
    public boolean isCredentialsNonExpired() {
        return true; // Or add logic for password expiration
    }
	
	public boolean isEnabled() {
        return true; // Or add logic to disable users
    }
}

/*
 * Notes  - 
 * 
 * nullable --> if false than does not allow null values
 * updatable --> value of this variable is set only once
 * @PrePersist --> Automatically sets the createdAt field before the record is inserted.
 * 
 * */



/*
 * 
 * Concept: The UserDetails Contract 🛡️
Spring Security is a powerful, generic framework designed to work with any application. 
It doesn't know or care that your user class is called Owner or that you use an email field for the username.

To solve this, Spring Security defines a "contract"—a Java interface called UserDetails.
This contract requires any user class to provide a standard set of methods, such as:

getUsername(): How do I get the user's main identifier?

getPassword(): How do I get the user's hashed password?

getAuthorities(): How do I get the user's list of roles/permissions?

isEnabled(): Is this user's account active?

When your Owner class implements UserDetails, you are essentially creating an adapter. 
You are teaching your custom Owner class how to "speak the language" that Spring Security understands.

Analogy: The Universal Travel Adapter
Think of it like this:

Your Owner class is like an Indian power plug.

Spring Security is like a European wall outlet.

The UserDetails interface is the universal travel adapter.

By implementing UserDetails, you are putting an adapter on your Owner plug. The methods you add 
(getUsername, getPassword) are the pins that connect your specific fields (email, password) to 
the standard slots in the Spring Security outlet.

Action: Why the Owner Entity Must Be Updated
You are not changing your data; you are simply adding the methods that fulfill the UserDetails contract.

getUsername(): You will implement this by simply returning this.email. This tells Spring Security, 
"For my application, the 'username' is the email."

getPassword(): You implement this by returning this.password. Spring Security needs this to compare 
against the password the user submits during login.

getAuthorities(): You implement this by converting your Role enum into a format Spring Security 
understands (GrantedAuthority).

Your AuthService interface is correct and does not need to be changed. This modification is purely 
at the entity level to integrate with the security framework.


 * */
