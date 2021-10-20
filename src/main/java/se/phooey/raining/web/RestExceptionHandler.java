package se.phooey.raining.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import se.phooey.raining.weather.exception.RainReportException;
import se.phooey.raining.web.exception.InvalidCoordinatesException;


@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
 
    @ExceptionHandler({InvalidCoordinatesException.class})
    public ResponseEntity<Object> handleBadRequest(
      Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getLocalizedMessage(), 
          new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
    
    @ExceptionHandler({RainReportException.class})
      public ResponseEntity<Object> handleInternalServerError(
        Exception ex, WebRequest request) {
          return handleExceptionInternal(ex, ex.getLocalizedMessage(), 
            new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
      }
}