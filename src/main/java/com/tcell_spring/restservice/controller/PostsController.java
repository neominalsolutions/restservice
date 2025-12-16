package com.tcell_spring.restservice.controller;
import com.tcell_spring.restservice.entity.Post;
import com.tcell_spring.restservice.repository.IPostRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// MAKALE: Controller katmanı, gelen HTTP isteklerini karşılar ve uygun servis katmanına yönlendirir.
// Makalerdeki işlemleri yönetir.
@RestController
@RequestMapping("/api/v1/posts") // resources için endpoint tanımı
public class PostsController {

    private final IPostRepository postRepository;

    public PostsController(IPostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // api/v1/posts -> HTTP GET -> List // 200
    @GetMapping
    public ResponseEntity<List<Post>> getPosts() {
        return ResponseEntity.ok(postRepository.findAll());
    }
    // @PathVariable -> Path üzerinden gelen parametreleri yakalamak için kullanılır.
    // api/v1/posts/1 -> HTTP GET -> Detail // 200
    @GetMapping("{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Integer id) {

        // eğer kayıt varsa 200 ile birlikte kaydı döner

       Optional<Post> postOptional = postRepository.findById(id);

       // optional ifadeler maplenebilir. streamler gibi düşünülebilir.
        return postOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

        // kayıt bulunamazsa 404 döner
    }

    // api/v1/posts -> HTTP POST -> Create // 201
    // @RequestBody ile gelen request body'sini yakalarız. dışarıdan uygulamaya json formatında veri gönderildiğinde bu anotasyon kullanılır.
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post request) {

       Post entity = postRepository.save(request);
       // new ResponseEntity<>(entity, HttpStatus.CREATED);
        return ResponseEntity.status(HttpStatus.CREATED).body(entity);
    }

    // api/v1/posts/1 -> HTTP PUT -> Update // 204
    @PutMapping("{id}")
    public String updatePost(@PathVariable Integer id, @RequestBody String request) {
        return "Post Updated";
    }

    // api/v1/posts/1/changeReleaseStatus -> HTTP PATCH -> Partial Update // 204
    @PatchMapping("{id}/changeReleaseStatus")
    public String changeReleaseStatus(@PathVariable Integer id, @RequestBody String request) {
        return "Post Partially Updated";
    }

    // api/v1/posts/1 -> HTTP DELETE -> Delete  // 204
    @DeleteMapping("{id}")
    public String deletePost(@PathVariable Integer id) {
        return "Post Deleted";
    }

}
