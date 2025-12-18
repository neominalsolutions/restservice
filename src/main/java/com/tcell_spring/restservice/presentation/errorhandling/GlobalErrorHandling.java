package com.tcell_spring.restservice.presentation.errorhandling;


import com.tcell_spring.restservice.domain.exception.SameEntityExistException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;

@RestControllerAdvice
public class GlobalErrorHandling {

    // MethodArgumentNotValidException ex -> Bunu takibe al, eğer bu tarz bir exception message gelirse git bu hatalar içerisinde hataya ait fieldleri bul, onların isimlerini ve mesajlarını topla ve kullanıcıya 400 bad request olarak dön.

    @ExceptionHandler(SameEntityExistException.class)
    public ResponseEntity<String> handleSameEntityExistException(SameEntityExistException ex) {
        // Hata mesajını loglayabilir veya farklı işlemler yapabilirsiniz.
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Aynı kayıt zaten mevcut: " + ex.getMessage());
    }


    // Uygulama eskiden controller seviyesinde tek tek bunları dönüyordu, şimdi global olarak burada toplanacak.
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
        // Hata mesajını loglayabilir veya farklı işlemler yapabilirsiniz.
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Kayıt bulunamadı: " + ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HashMap<String,List<String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Hata mesajını loglayabilir veya farklı işlemler yapabilirsiniz.

        HashMap<String, List<String>> validationErrors = new HashMap<>();

        // Tüm field hatalarını dolaş [{fieldName: [error1, error2]}]

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();

            // eğer bu field için daha önce hata eklenmemişse, yeni bir liste oluştur
            if (!validationErrors.containsKey(fieldName)) {
                validationErrors.put(fieldName, new java.util.ArrayList<>());
            }

            // eğer key için hata mesajı zaten yoksa ekle
            validationErrors.get(fieldName).add(errorMessage);
        });


        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationErrors);
    }


//    @ExceptionHandler(DataIntegrityViolationException.class)
//    public String handleAllExceptions(DataIntegrityViolationException ex) {
//        // Hata mesajını loglayabilir veya farklı işlemler yapabilirsiniz.
//        return "Bir hata oluştu: " + ex.getMessage();
//    }

}
