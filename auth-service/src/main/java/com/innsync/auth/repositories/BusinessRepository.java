package com.innsync.auth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.innsync.auth.entities.Business;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long>{
	
}
