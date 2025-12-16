package com.tcell_spring.restservice.request.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

// field isimlerini entity isimleri ile kolay maplemek açısında birebir aynı yapıyoruz.
// json olarak dışarı çıkartırken daha anlamılı ve veri modelimiz aynı şekilde yansıtmayacak isimler tanımlıyoruz.

@Data
@AllArgsConstructor
public class PostCreateRequest {

    @NotBlank(message = "Title alanı boş olamaz.")
    @NotNull(message = "Title alanı null olamaz.")
    @NotEmpty(message = "Title alanı empty olamaz.")
    @Size(max = 50, message = "Title alanı en fazla 50 karakter olabilir.")
    @Pattern(regexp = "^[A-Za-z0-9ğüşöçıİĞÜŞÖÇ\\s]+$", message = "Title alanı sadece harf ve rakamlardan oluşabilir.")
    @JsonProperty("post_title")
    private String title;

    @JsonProperty("post_content")
    @NotEmpty(message = "Content alanı boş olamaz.")
    @NotNull(message = "Content alanı null olamaz.")
    @NotBlank(message = "Content alanı empty olamaz.")
    @Size(max = 500, message = "Content alanı en fazla 500 karakter olabilir.")
    private String content;

    @JsonProperty("post_released")
    @NotNull(message = "isReleased alanı null olamaz.")
    private Boolean isReleased;

    @JsonProperty("post_release_date")
    @NotNull(message = "releaseDate alanı null olamaz.")
//    @PastOrPresent(message = "releaseDate alanı geçmiş veya şimdiki zaman olmalıdır.")
    @Future(message = "releaseDate alanı gelecek zaman olmalıdır.")
    private LocalDateTime releaseDate;

}
