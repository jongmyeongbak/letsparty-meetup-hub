package com.letsparty.exception;

public class LetsPartyException extends RuntimeException{

	private static final long serialVersionUID = -5814264735411696467L;

	public LetsPartyException(String message) {
		super(message);
	}
	
	public LetsPartyException(String message, Throwable cause) {
		super(message, cause);
	}
}
