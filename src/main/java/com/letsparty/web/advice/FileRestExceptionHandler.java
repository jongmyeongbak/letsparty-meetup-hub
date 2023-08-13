package com.letsparty.web.advice;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.letsparty.dto.ErrorResponse;
import com.letsparty.web.controller.FileRestController;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice(basePackageClasses = FileRestController.class)
@Slf4j
public class FileRestExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(final IllegalArgumentException e, final HttpServletRequest request) {
		log.error("Invalid argument provided -> uri:{}", request.getRequestURI());
		return ResponseEntity.badRequest().body(ErrorResponse.of(400, "BAD_REQUEST", "Unsupported path.", request.getRequestURI()));
	}
	
	@ExceptionHandler(IOException.class)
	public ResponseEntity<ErrorResponse> handleIOException(final IOException e, final HttpServletRequest request) {
		log.error("I/O error -> uri:{}", request.getRequestURI());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.of(500, "INTERNAL_ERROR", "An internal server error occurred.", request.getRequestURI()));
	}
}
