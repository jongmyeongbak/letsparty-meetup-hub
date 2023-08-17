package com.letsparty.exception;

public class DuplicateEmailException extends LetsPartyException {

	private static final long serialVersionUID = 6924195208055068269L;

	public DuplicateEmailException(String message) {
		super(message);
	}
}
