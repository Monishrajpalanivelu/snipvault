package com.snippetvault.snipvault.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SnippetRequest {

    @NotBlank(message = "Title Is Required")  // By hibernate validator dependency
    @Size(max = 100,message="Title must be 100 characters or fewer")
    private String title;

    @NotBlank(message = "Code cannot be empty")
    private String code;

    @NotBlank(message = "Language is required")
    private String language;
    private String description;

}
