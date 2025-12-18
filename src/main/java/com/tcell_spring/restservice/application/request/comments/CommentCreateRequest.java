package com.tcell_spring.restservice.application.request.comments;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreateRequest {

    @JsonProperty( "post_id" )
    private Integer postId;

    @JsonProperty( "comment_content" )
    private String content;

}
