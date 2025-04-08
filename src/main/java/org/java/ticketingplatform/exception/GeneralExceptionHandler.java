package org.java.ticketingplatform.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.http.ResponseEntity;

@ControllerAdvice
public class GeneralExceptionHandler {

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<String> missingApuParameter(MissingServletRequestParameterException ex){
		String name = ex.getParameterName();
		return ResponseEntity.badRequest().body("Missing required parameter: " + name);
	}
}
