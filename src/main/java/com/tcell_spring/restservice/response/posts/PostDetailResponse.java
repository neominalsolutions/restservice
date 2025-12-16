package com.tcell_spring.restservice.response.posts;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tcell_spring.restservice.response.comments.CommentDetailResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDetailResponse {

    @JsonProperty("post_id")
    private Integer id;

    @JsonProperty("post_title")
    private String title;

    @JsonProperty("post_content")
    private String content;

    @JsonProperty("post_released")
    private Boolean isReleased;

    @JsonProperty("post_release_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime releaseDate;

    @JsonProperty("post_comments")
    private List<CommentDetailResponse> comments;

}
