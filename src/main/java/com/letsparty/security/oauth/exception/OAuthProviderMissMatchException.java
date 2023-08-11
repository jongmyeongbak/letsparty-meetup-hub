package com.letsparty.security.oauth.exception;

public class OAuthProviderMissMatchException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public OAuthProviderMissMatchException(String message) {
		super(message);
	}
}
