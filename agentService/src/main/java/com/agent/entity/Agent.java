package com.agent.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
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
public class Agent {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int agentid;
	private String agentName;
	private String agentState;
	private String linkedBankAccountName;
	private String email;
	private int walletBalance;
	@Column
    @ElementCollection(targetClass=Integer.class)
	List<Client> clients=new ArrayList<>();
	
}
