package com.tcell_spring.restservice.request.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

// field isimlerini entity isimleri ile kolay maplemek açısında birebir aynı yapıyoruz.
// json olarak dışarı çıkartırken daha anlamılı ve veri modelimiz aynı şekilde yansıtmayacak isimler tanımlıyoruz.

@Data
@AllArgsConstructor
public class PostCreateRequest {

    @JsonProperty("post_title")
    private String title;

    @JsonProperty("post_content")
    private String content;

    @JsonProperty("post_released")
    private Boolean isReleased;

    @JsonProperty("post_release_date")
    private LocalDateTime releaseDate;

}
