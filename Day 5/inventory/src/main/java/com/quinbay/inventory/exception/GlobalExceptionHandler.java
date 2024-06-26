package com.quinbay.inventory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.quinbay.inventory.exception.CustomExceptions.ProductNotFoundException;
import com.quinbay.inventory.exception.CustomExceptions.InvalidProductDataException;
import com.quinbay.inventory.exception.CustomExceptions.NoProductsAvailableException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<String> handleProductNotFoundException(CustomExceptions.ProductNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(CustomExceptions.InvalidProductDataException.class)
    public ResponseEntity<String> handleInvalidProductDataException(InvalidProductDataException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(NoProductsAvailableException.class)
    public ResponseEntity<String> handleNoProductsAvailableException(NoProductsAvailableException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
    }
}
