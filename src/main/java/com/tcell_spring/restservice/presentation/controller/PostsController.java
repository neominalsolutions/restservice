package com.tcell_spring.restservice.presentation.controller;
import com.tcell_spring.restservice.application.handler.PostRequestHandler;
import com.tcell_spring.restservice.domain.entity.Post;
import com.tcell_spring.restservice.application.request.comments.CommentCreateRequest;
import com.tcell_spring.restservice.application.request.posts.PostChangeReleasedRequest;
import com.tcell_spring.restservice.application.request.posts.PostCreateRequest;
import com.tcell_spring.restservice.application.request.posts.PostUpdateRequest;
import com.tcell_spring.restservice.application.response.comments.CommentDetailResponse;
import com.tcell_spring.restservice.application.response.posts.PostCreateResponse;
import com.tcell_spring.restservice.application.response.posts.PostDetailResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// MAKALE: Controller katmanı, gelen HTTP isteklerini karşılar ve uygun servis katmanına yönlendirir.
// Makalerdeki işlemleri yönetir.
// Bu controller sorumluluğu ise doğru usecase'e yönlendirmektir.
@RestController
@RequestMapping("/api/v1/posts") // resources için endpoint tanımı
@Tag(name = "Posts", description = "Operations related to Posts")
public class PostsController {

    private final PostRequestHandler postRequestHandler;

    public PostsController(PostRequestHandler postRequestHandler) {
        this.postRequestHandler = postRequestHandler;
    }

    // api/v1/posts -> HTTP GET -> List // 200
    @GetMapping
    public ResponseEntity<List<PostDetailResponse>> getPosts() {
        List<PostDetailResponse> response = postRequestHandler.getAllPosts();
        return ResponseEntity.ok(response);
    }
    // @PathVariable -> Path üzerinden gelen parametreleri yakalamak için kullanılır.
    // api/v1/posts/1 -> HTTP GET -> Detail // 200
    @GetMapping("{id}")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Post found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Post not found")
    })
    @Operation(summary = "Get Post by ID", description = "Retrieve a post by its unique ID")
    public ResponseEntity<PostDetailResponse> getPostById(@PathVariable Integer id) {
        // eğer kayıt varsa 200 ile birlikte kaydı döner
      return ResponseEntity.ok(postRequestHandler.getPostById(id));
    }

    // api/v1/posts -> HTTP POST -> Create // 201
    // @RequestBody ile gelen request body'sini yakalarız. dışarıdan uygulamaya json formatında veri gönderildiğinde bu anotasyon kullanılır.
    // @Valid anatasyonu ile request objesi üzerinde tanımlanan validation kuralları çalıştırılır.
    @PostMapping
    public ResponseEntity<PostCreateResponse> createPost(@Valid @RequestBody PostCreateRequest request) {
        PostCreateResponse response = postRequestHandler.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // api/v1/posts/1 -> HTTP PUT -> Update // 204
    @PutMapping("{id}")
    public ResponseEntity<Void> updatePost(@PathVariable Integer id, @RequestBody PostUpdateRequest request) throws BadRequestException {
            postRequestHandler.updatePost(id,request);
            return ResponseEntity.noContent().build();
    }

    // api/v1/posts/1/changeReleaseStatus -> HTTP PATCH -> Partial Update // 204
    @PatchMapping("{id}/changeReleaseStatus")
    public ResponseEntity<Void> changeReleaseStatus(@PathVariable Integer id, @RequestBody PostChangeReleasedRequest request) {
        postRequestHandler.changePostReleaseStatus(id, request);
        return ResponseEntity.noContent().build();
    }

    // Senaryo -> Eğer altında comment bulunan entity silinirse ne olur ? Cascade ayarları nasıl olmalı ?
    // Üst nesne -> Post silindiği için Posta ait tüm alt nesnelerde (Comment) silindi.
    // api/v1/posts/1 -> HTTP DELETE -> Delete  // 204
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Integer id) {
        postRequestHandler.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    // Nested Routes - Comments
    // api/v1/posts/1/comments -> HTTP GET -> Get Post Comments // 200
    @GetMapping("{postId}/comments")
    public ResponseEntity<List<CommentDetailResponse>> getPostComments(@PathVariable Integer postId) {

       List<CommentDetailResponse> response = postRequestHandler.getCommentsByPostId(postId);
        return ResponseEntity.ok(response);
    }

    // api/v1/posts/1/comments -> HTTP POST -> Create Post Comment // 201
    @PostMapping("{postId}/comments")
    public ResponseEntity<CommentDetailResponse> createPostComment(@PathVariable Integer postId,@RequestBody CommentCreateRequest request) {
        CommentDetailResponse response = postRequestHandler.addCommentToPost(postId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Not: Test etmek amaçlı yapıldı.
    // api/v1/posts/withComments -> HTTP POST -> Create Post with Comments //

    // Not: CascadeType.PERSIST veya ALL seçili olmasaydı bu özellikten faydalanamazdık.
    @PostMapping("withComments")
    public ResponseEntity<Post> createPostWithComments() {
        Post response = postRequestHandler.createPostWithComments();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    // Çoklu verilerde sayfa işlemlerini JPA üzerinden nasıl yönetiriz ?
    // Pagination, Sorting, Filtering işlemleri nasıl yapılır ?
    // Bu işlemler için Spring Data JPA'nın sağladığı Pageable interface'i kullanılır.
    // Bu konu ilerleyen derslerde detaylı olarak ele alınacaktır.

    // api/v1/posts/paged?page=0&size=10 -> HTTP GET -> Get Posts Paged // 200
    //  @RequestParam -> Query string üzerinden gelen parametreleri yakalamak için kullanılır.
    // localhost:8080/api/v1/posts/q?page=2&size=5&sortBy=title&sortDir=desc
    @GetMapping("q")
    public ResponseEntity<Page<PostDetailResponse>> getPostsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "" ) String title
    ) {

        Page<PostDetailResponse> postPage = postRequestHandler.getPagedPosts(page, size, sortBy, sortDir, title);
         return ResponseEntity.ok(postPage);

    }


    // cohesion artırmak için yorum oluşturma işlemi burada yapıldı.
    // Bu sayede bir post ile ilgili tüm işlemler tek controllerda toplanmış oldu.

    // Bir yazılımın kalite standartı genel olarak 2 faktöre bağlıdır:
    // Cohesion (İçsel Bağlılık): Bir modülün veya bile
    // Coupling (Bağımlılık): Modüller arasındaki bağımlılıkların azlığı.
    // Düşük coupling, modüllerin birbirinden bağımsız çalışabilmesini sağlar
    // Yüksek cohesion ise bir modülün kendi içinde tutarlı ve odaklanmış olmasını ifade eder.
    // Yüksek cohesion ve düşük coupling, yazılımın bakımını, genişletilmesini ve test edilmesini kolaylaştırır.
    // Post ile Comment kaydını aynı transaction içerisinde Nasıl yapılabilir.
    // Bu örneği yapalım -> CascadeType.PERSIST işe yarayacak.

}
