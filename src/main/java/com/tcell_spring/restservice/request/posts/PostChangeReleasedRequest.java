package com.tcell_spring.restservice.request.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostChangeReleasedRequest {

    @JsonProperty("post_id")
    private Integer postId;

    @JsonProperty("post_released")
    private Boolean isReleased;

}
