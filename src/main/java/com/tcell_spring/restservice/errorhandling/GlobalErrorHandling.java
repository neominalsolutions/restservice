package com.tcell_spring.restservice.errorhandling;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalErrorHandling {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleAllExceptions(DataIntegrityViolationException ex) {
        // Hata mesajını loglayabilir veya farklı işlemler yapabilirsiniz.
        return "Bir hata oluştu: " + ex.getMessage();
    }

}
