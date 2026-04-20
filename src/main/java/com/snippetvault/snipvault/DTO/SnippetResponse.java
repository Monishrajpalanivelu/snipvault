package com.snippetvault.snipvault.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SnippetResponse implements Serializable {
    private Long id;
    private String title;
    private String code;
    private String language;
    private String description;
    private LocalDateTime createdAt;
}
