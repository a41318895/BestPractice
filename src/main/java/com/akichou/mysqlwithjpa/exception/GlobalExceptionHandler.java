package com.akichou.mysqlwithjpa.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(exception = ProductNotFoundException.class)
    public ResponseEntity<Object> productNotFoundExceptionHandler(ProductNotFoundException ex) {

        log.error(ex.getMessage()) ;

        return ResponseEntity.badRequest().body(ex.getMessage()) ;
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<String> handleConcurrencyIssue(OptimisticLockingFailureException ex) {

        log.error(ex.getMessage()) ;

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Concurrency Conflict occurred. Please try again later.") ;
    }

    @ExceptionHandler(exception = Exception.class)
    public ResponseEntity<Object> exceptionHandler(Exception ex) {

        log.error(ex.getMessage()) ;

        return ResponseEntity.status(500).body(ex.getMessage()) ;
    }
}
