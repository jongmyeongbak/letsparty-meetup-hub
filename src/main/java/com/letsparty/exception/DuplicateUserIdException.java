package com.letsparty.exception;

public class DuplicateUserIdException extends LetsPartyException {

	private static final long serialVersionUID = -5044977885785463283L;

	public DuplicateUserIdException(String message) {
		super(message);
	}

}
