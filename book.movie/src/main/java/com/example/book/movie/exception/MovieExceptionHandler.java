package com.example.book.movie.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice
public class MovieExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(MovieExceptionHandler.class);


	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> movieException(Exception e) {
		logger.error("error occurred {}", e);
		return new ResponseEntity<>("Exception : " + e.getMessage(), HttpStatus.I_AM_A_TEAPOT);
	}

}
