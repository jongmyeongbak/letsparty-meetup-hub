package com.letsparty.exception;

public class PostNotFoundException extends LetsPartyException {
	
	private static final long serialVersionUID = 1589492590487675553L;

	public PostNotFoundException(String message) {
		super(message);
	}

}
