package com.snippetvault.snipvault.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SnippetActivityEvent {

    private String action;      // "CREATED", "UPDATED", "DELETED"
    private String username;    // who did it
    private String snippetTitle; // which snippet
    private String language;    // what language
    private LocalDateTime timestamp; // when
}