package com.snippetvault.snipvault;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO )
public class WebConfig {

}
/*
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO), you are telling Spring:

"Hey, when you turn a Page into JSON, use the clean, standardized format."
{
  "content": [...],
  "page": {
    "size": 10,
    "number": 0,
    "totalElements": 50,
    "totalPages": 5
  }
}
 */
