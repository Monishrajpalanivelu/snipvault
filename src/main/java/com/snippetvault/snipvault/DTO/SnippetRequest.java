package com.snippetvault.snipvault.DTO;

import lombok.Data;

@Data
public class SnippetRequest {

    private String title;
    private String code;
    private String language;
    private String description;

}
