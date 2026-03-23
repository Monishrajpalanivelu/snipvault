package com.snippetvault.snipvault.Exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor              // Doesnt need field to be final . create constructor for all the fields
public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
}

// Shape/Structures all the error messages in one format