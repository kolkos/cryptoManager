package nl.kolkos.cryptoManager.controllers;

import java.util.Date;
import java.util.LinkedHashMap;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
 
    @ExceptionHandler(value = { IllegalArgumentException.class, IllegalStateException.class })
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        LinkedHashMap<String, Object> errorMsg = new LinkedHashMap<>();
        errorMsg.put("error", ex.getClass());
        errorMsg.put("message", ex.getMessage());
        errorMsg.put("date", new Date());
        
        return handleExceptionInternal(ex, errorMsg, 
          new HttpHeaders(), HttpStatus.CONFLICT, request);
    }
}
