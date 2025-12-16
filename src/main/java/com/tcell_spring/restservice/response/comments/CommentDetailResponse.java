package com.tcell_spring.restservice.response.comments;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentDetailResponse{
    @JsonProperty( "comment_id" )
    public Integer id;

    @JsonProperty( "comment_content" )
    public String content;

    @JsonProperty( "post_id" )
    private Integer postId;
}
