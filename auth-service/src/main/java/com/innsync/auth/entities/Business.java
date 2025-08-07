package com.innsync.auth.entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name="businesses", 
		uniqueConstraints = @UniqueConstraint(columnNames = {"gst_number"})
		)
public class Business {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Integer id;
		
		@Column(nullable = false)
		private String businessName;
		
		@Column(nullable = false)
		private String businessType;
		
		@Column(nullable = true)
		private String gstNumber;
		
		@Embedded
		private Address address;
		
		@Column(name = "createdAt", nullable = false, updatable = false)
		private LocalDateTime createdAt;
		@PrePersist
		protected void onCreate() {
			this.createdAt=LocalDateTime.now();
		}
		
		//FK reference of owner id from owners table 
		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(name="ownerId", nullable = false)
		@OnDelete(action = OnDeleteAction.CASCADE)
		private Owner owner;
		
		@Column(nullable = true,unique = true)
		private String dbIdentifier;
		
		
}
