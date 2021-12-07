package com.fundtransfer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.NOT_FOUND)
public class AccountNotFoundException extends RuntimeException {

	static final long serialVersionUID = 1L;
	
	public AccountNotFoundException(String message) {
		super(message);
	}

}