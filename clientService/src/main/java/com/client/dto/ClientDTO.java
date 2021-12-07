package com.client.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {
	private String clientid;
	private String clientName;
	private String clientState;
	private String email;
	private String bankAccountName;

}
