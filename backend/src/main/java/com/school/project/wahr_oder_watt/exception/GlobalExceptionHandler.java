package com.school.project.wahr_oder_watt.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.text.ParseException;

/**
 * Globale Exception-Handler für das Projekt.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ParseException.class)
  public ResponseEntity<String> handleParseException(ParseException ex) {
    return ResponseEntity.badRequest().body("Ungültiges Datumsformat: " + ex.getMessage());
  }

  // Weitere Exception-Handler können hier ergänzt werden
}