package com.client.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Client {
		
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private int clientid;
		private String clientName;
		private String clientState;
		private String bankAccountName;
		private int walletBalance;
		private String email;
		private int agentId;

}

