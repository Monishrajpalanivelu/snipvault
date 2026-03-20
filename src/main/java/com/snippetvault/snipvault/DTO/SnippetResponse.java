package com.snippetvault.snipvault.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SnippetResponse {
    private Long id;
    private String title;
    private String code;
    private String language;
    private String description;
    private LocalDateTime createdAt;
}
