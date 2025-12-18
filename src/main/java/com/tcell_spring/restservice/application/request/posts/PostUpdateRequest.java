package com.tcell_spring.restservice.application.request.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostUpdateRequest {

    @JsonProperty("post_id")
    private Integer id;

    @JsonProperty("post_title")
    private String title;

    @JsonProperty("post_content")
    private String content;

    @JsonProperty("post_released")
    private Boolean isReleased;

}
