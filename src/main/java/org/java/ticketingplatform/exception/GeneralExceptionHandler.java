package org.java.ticketingplatform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GeneralExceptionHandler {

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<String> handleMissingParameter(MissingServletRequestParameterException ex) {
		String name = ex.getParameterName();
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body("Missing required parameter: " + name);
	}

	@ExceptionHandler(SeatOccupiedException.class)
	public ResponseEntity<String> handleSeatOccupiedException(SeatOccupiedException ex) {
		String errorMessage = "Seat Occupied: " + ex.getMessage();
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body("Redis Error" + errorMessage);
	}
}
