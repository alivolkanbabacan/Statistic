package com.n26.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import lombok.extern.java.Log;

/**
 * Global Exception handler that handles Exceptions thrown from Controllers.
 * 
 */
@Log
@ControllerAdvice
public class CustomExceptionHandler {

	@ExceptionHandler(OutDatedTransactionException.class)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void handleOutDatedTransactionException(OutDatedTransactionException ex) {
		log.severe(ex.getMessage());
	}

	@ExceptionHandler({ IllegalArgumentException.class, InvalidFormatException.class })
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	public void handleUnprocessableEntityErrors(Exception ex) {
		log.severe(ex.getMessage());
	}

	@ExceptionHandler({ MismatchedInputException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handleMismatchedInputException(MismatchedInputException ex) {
		log.severe(ex.getMessage());
	}
}
