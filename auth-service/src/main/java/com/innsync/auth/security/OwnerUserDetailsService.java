package com.innsync.auth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.innsync.auth.repositories.OwnerRepository;

@Service
public class OwnerUserDetailsService implements UserDetailsService{
	
	@Autowired
	private OwnerRepository ownerRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// The 'username' parameter can now be either an email or a phone number.

        // A simple regular expression to check if the input string is a 10-digit number.
        String phoneRegex = "^\\d{10}$";
        
        if (username.matches(phoneRegex)) {
            // If the input matches the phone number format, search by phone.
            return ownerRepository.findByPhone(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + username));
        } else {
            // Otherwise, assume it's an email and search by email.
            return ownerRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        }
    }

}
