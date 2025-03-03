package com.bornfire.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ServerErrorException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ServerErrorException(String message) {
		super(message);
	}
}
