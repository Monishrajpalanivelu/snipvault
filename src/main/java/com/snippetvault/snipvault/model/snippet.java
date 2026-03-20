package com.snippetvault.snipvault.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data                                   // Provide getters/setters
@Entity                                 // Make the class as DB table
@Table(name="snippets")                 // Name of the table
public class snippet {

    @Id                                 // Primary Key
    @GeneratedValue(strategy= GenerationType.IDENTITY)  // Automatic ID generation (primary key)
    private Long id;

    @Column(nullable = false)           // Column definition
    private String title;
    @Column(nullable = false,columnDefinition = "TEXT")  //columnDefinition -> Datatype
    private String code;
    @Column(nullable = false)
    private String language;

    private String description;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    @PrePersist                         // Cant be modified after creation
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
