package com.letsparty.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ErrorResponse {

	private final LocalDateTime timestamp;
	private final int status;
	private final String errorCode;
	private final String message;
	private final String requestURI;
	
	@Builder
	private ErrorResponse(final int status, final String errorCode, final String message, final String requestURI) {
		super();
		this.timestamp = LocalDateTime.now();
		this.status = status;
		this.errorCode = errorCode;
		this.message = message;
		this.requestURI = requestURI;
	}
	
	public static ErrorResponse of(final String errorCode, final String message) {
		return ErrorResponse.builder().errorCode(errorCode).message(message).build();
	}
	
	public static ErrorResponse of(final int status, final String errorCode, final String message, final String requestURI) {
		return ErrorResponse.builder().status(status).errorCode(errorCode).message(message).requestURI(requestURI).build();
	}
}
