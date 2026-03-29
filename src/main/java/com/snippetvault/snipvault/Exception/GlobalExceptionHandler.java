/*


DRY principle — Don't Repeat Yourself — in action. so single exception instead of multiple try-catch

*/
package com.snippetvault.snipvault.Exception;

import com.snippetvault.snipvault.DTO.SnippetResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice  /*

Tells Spring: this class handles exceptions thrown anywhere in any controller.
 One class catches errors from all 5 endpoints.
 Without this, each controller method needs its own try-catch.

*/
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {

        Map<String, String> fieldErrors = new HashMap<>();
        for(FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        String errorMessages = fieldErrors.values().stream()
                .collect(Collectors.joining(" , "));
        ErrorResponse response=new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                errorMessages,
                LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(response);

    }

    //snippet not found
    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFound exception) {
        ErrorResponse error=new ErrorResponse(
                404,
                exception.getMessage(),
                LocalDateTime.now()
        );
        return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);

    }

    // missing fields or invalid request body
    @ExceptionHandler(IllegalArgumentException.class)  //Maps a specific exception type to a handler method.
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException exception) {
        ErrorResponse error=new ErrorResponse(
                400,
                exception.getMessage(),
                LocalDateTime.now()
        );
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

    }

    // anything unexpected
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception exception) {

        ErrorResponse error=new ErrorResponse(
                500,
                "Something went wrong. Please try again.",
                LocalDateTime.now()
        );
        return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
