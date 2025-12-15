package com.tcell_spring.restservice.controller;
import org.springframework.web.bind.annotation.*;

// MAKALE: Controller katmanı, gelen HTTP isteklerini karşılar ve uygun servis katmanına yönlendirir.
// Makalerdeki işlemleri yönetir.
@RestController
@RequestMapping("/api/v1/posts") // resources için endpoint tanımı
public class PostsController {

    // api/v1/posts -> HTTP GET -> List // 200
    @GetMapping
    public String getPosts() {
        return "List of Posts";
    }
    // @PathVariable -> Path üzerinden gelen parametreleri yakalamak için kullanılır.
    // api/v1/posts/1 -> HTTP GET -> Detail // 200
    @GetMapping("/{id}")
    public String getPostById(@PathVariable Integer id) {
        return "Post with ID: " + id;
    }

    // api/v1/posts -> HTTP POST -> Create // 201
    // @RequestBody ile gelen request body'sini yakalarız. dışarıdan uygulamaya json formatında veri gönderildiğinde bu anotasyon kullanılır.
    @PostMapping
    public String createPost(@RequestBody String request) {
        return "Post Created";
    }

    // api/v1/posts/1 -> HTTP PUT -> Update // 204
    @PutMapping("{id}")
    public String updatePost(@PathVariable Integer id, @RequestBody String request) {
        return "Post Updated";
    }

    // api/v1/posts/1/changeReleaseStatus -> HTTP PATCH -> Partial Update // 204
    @PatchMapping("/{id}/changeReleaseStatus")
    public String changeReleaseStatus(@PathVariable Integer id, @RequestBody String request) {
        return "Post Partially Updated";
    }

    // api/v1/posts/1 -> HTTP DELETE -> Delete  // 204
    @DeleteMapping("{id}")
    public String deletePost(@PathVariable Integer id) {
        return "Post Deleted";
    }

}
