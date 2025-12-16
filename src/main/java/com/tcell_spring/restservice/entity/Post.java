package com.tcell_spring.restservice.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Data // Getter ve Setter metodlarını otomatik olarak oluşturur.
@AllArgsConstructor
@NoArgsConstructor
public class Post {

    @Id // Entity'nin birincil anahtarını belirtir. PK
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50, unique = true)
    private String title;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isReleased;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime releaseDate;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Comment> comments;
    // null reference hatasını önlemek için boş bir liste ile başlatıldı.
}

// bidirectional ilişki: Post ve Comment entity'leri arasında çift yönlü bir ilişki kurulmuş olur.
// Yani, bir Post nesnesi üzerinden ilişkili Comment nesnelerine erişilebilir ve
// aynı şekilde bir Comment nesnesi üzerinden de ilişkili Post nesnesine erişilebilir.
// Bu, veri modelinin daha esnek ve kullanışlı olmasını sağlar.