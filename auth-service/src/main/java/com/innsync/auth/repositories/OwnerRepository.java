package com.innsync.auth.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.innsync.auth.entities.Owner;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Integer>{
	// This method will be automatically implemented by Spring Data JPA
    Optional<Owner> findByEmail(String email);
    
    Optional<Owner> findByPhone(String phone);
    
    Optional<Owner> findByPasswordResetToken(String token);
}


/*
 *findByEmail(String email): Spring parses this method name and understands it needs to query the Owner 
 *entity for a record where the email column matches the provided string.

Optional<Owner>: This is a modern Java practice. Instead of returning null if no owner is found 
(which can lead to NullPointerException), this method returns an Optional container, which may or 
may not hold an Owner object. It forces us to handle the "not found" case explicitly and safely. 
 * 
 * */
