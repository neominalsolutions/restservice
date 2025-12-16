package com.tcell_spring.restservice.controller;
import com.tcell_spring.restservice.entity.Comment;
import com.tcell_spring.restservice.entity.Post;
import com.tcell_spring.restservice.repository.ICommentRepository;
import com.tcell_spring.restservice.repository.IPostRepository;
import com.tcell_spring.restservice.request.posts.PostCreateRequest;
import com.tcell_spring.restservice.request.posts.PostUpdateRequest;
import com.tcell_spring.restservice.response.comments.CommentDetailResponse;
import com.tcell_spring.restservice.response.posts.PostCreateResponse;
import com.tcell_spring.restservice.response.posts.PostDetailResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// MAKALE: Controller katmanı, gelen HTTP isteklerini karşılar ve uygun servis katmanına yönlendirir.
// Makalerdeki işlemleri yönetir.
@RestController
@RequestMapping("/api/v1/posts") // resources için endpoint tanımı
public class PostsController {

    private final IPostRepository postRepository;
    private final ICommentRepository commentRepository;

    public PostsController(IPostRepository postRepository, ICommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    // api/v1/posts -> HTTP GET -> List // 200
    @GetMapping
    public ResponseEntity<List<PostDetailResponse>> getPosts() {

        List<PostDetailResponse> response = postRepository.findAll().stream().map(postEntity ->
             new PostDetailResponse(
                    postEntity.getId(),
                    postEntity.getTitle(),
                    postEntity.getContent(),
                    postEntity.getIsReleased(),
                    postEntity.getReleaseDate(),
                    null // yorumlar eklenmedi -> Liste ekranları Data Grid üzerinde gösterilir detay gösterecek kadar alan yoktur. Fetch performansı için yorumlar eklenmedi. Tek bir sorgu ile sadece Post verileri çekildi.
            )).toList();

        return ResponseEntity.ok(response);
    }
    // @PathVariable -> Path üzerinden gelen parametreleri yakalamak için kullanılır.
    // api/v1/posts/1 -> HTTP GET -> Detail // 200
    @GetMapping("{id}")
    public ResponseEntity<PostDetailResponse> getPostById(@PathVariable Integer id) {

        // eğer kayıt varsa 200 ile birlikte kaydı döner

       Optional<Post> postOptional = postRepository.findById(id);

       if(postOptional.isPresent()) {
           PostDetailResponse response = new PostDetailResponse();
           // Response objesine entity'deki verileri kopyala
           BeanUtils.copyProperties(postOptional.get(), response);
           // modelmapper yerine spring'in kendi BeanUtils kütüphanesi kullanıldı. Performans açısından daha iyi sonuç verir.

          List<CommentDetailResponse> commentResponse = postOptional.get().getComments().stream().map(commentEntity-> {
                       CommentDetailResponse commentDto = new CommentDetailResponse();
                       BeanUtils.copyProperties(commentEntity, commentDto);
                       return commentDto;
                   }).toList();

           response.setComments(commentResponse);

           return ResponseEntity.ok(response);
       } else {
           // kayıt bulunamazsa 404 döner
           return ResponseEntity.notFound().build();
       }
    }

    // api/v1/posts -> HTTP POST -> Create // 201
    // @RequestBody ile gelen request body'sini yakalarız. dışarıdan uygulamaya json formatında veri gönderildiğinde bu anotasyon kullanılır.
    @PostMapping
    public ResponseEntity<PostCreateResponse> createPost(@RequestBody PostCreateRequest request) {

        Post postEntity = new Post();
        BeanUtils.copyProperties(request,postEntity);  // request'ten entity'e verileri kopyala

       Post entity = postRepository.save(postEntity);
       // new ResponseEntity<>(entity, HttpStatus.CREATED);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new PostCreateResponse(entity.getId(), LocalDateTime.now())
        );
    }

    // api/v1/posts/1 -> HTTP PUT -> Update // 204
    @PutMapping("{id}")
    public ResponseEntity<Void> updatePost(@PathVariable Integer id, @RequestBody PostUpdateRequest request) {


        Optional<Post> postOptional = postRepository.findById(id);

        // update işlemleri için ekstra veri tutarlılığı kontrolleri yapılabilir.
        if(request.getId() != null && !request.getId().equals(id)) {
            // request body'sindeki id ile path variable'daki id uyuşmuyorsa bad request döneriz.
            return ResponseEntity.badRequest().build();
        }

        if(postOptional.isPresent()) {
            // idsine göre bulduğu entity'nin verilerini request'ten gelen verilerle güncelle
            // postOptional.get() -> entity

            BeanUtils.copyProperties(request,postOptional.get());
            postRepository.save(postOptional.get()); // save hem create hem update için kullanılır. id veritabanında eşleşiyorsa update yapar.
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    // api/v1/posts/1/changeReleaseStatus -> HTTP PATCH -> Partial Update // 204
    @PatchMapping("{id}/changeReleaseStatus")
    public ResponseEntity<Void> changeReleaseStatus(@PathVariable Integer id, @RequestBody Boolean request) {
        Optional<Post> postOptional = postRepository.findById(id);

        if(postOptional.isPresent()) {
            Post post = postOptional.get();
            post.setIsReleased(request);
            postRepository.save(post);
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    // Senaryo -> Eğer altında comment bulunan entity silinirse ne olur ? Cascade ayarları nasıl olmalı ?
    // Üst nesne -> Post silindiği için Posta ait tüm alt nesnelerde (Comment) silindi.
    // api/v1/posts/1 -> HTTP DELETE -> Delete  // 204
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Integer id) {

        Optional<Post> postOptional = postRepository.findById(id);

        if(postOptional.isPresent()) {
            postRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    // Nested Routes - Comments
    // api/v1/posts/1/comments -> HTTP GET -> Get Post Comments // 200
    @GetMapping("{postId}/comments")
    public ResponseEntity<List<CommentDetailResponse>> getPostComments(@PathVariable Integer postId) {
        // Bu örnekte, yorumlar sabit bir liste olarak döndürülüyor.
        // Gerçek bir uygulamada, yorumlar veritabanından veya başka bir kaynaktan alınır.
        List<CommentDetailResponse> response = commentRepository.findByPostId(postId).stream().map(commentEntity ->{
            CommentDetailResponse commentResponse = new CommentDetailResponse();
            BeanUtils.copyProperties(commentEntity, commentResponse);
            return commentResponse;
        }).toList();

        return ResponseEntity.ok(response);
    }

    // api/v1/posts/1/comments -> HTTP POST -> Create Post Comment // 201
    @PostMapping("{postId}/comments")
    public ResponseEntity<Comment> createPostComment(@PathVariable Integer postId,@RequestBody Comment request) {

        Optional<Post> optionalPost = postRepository.findById(postId);

        if(optionalPost.isPresent()) {
            Post post = optionalPost.get();
            request.setPost(post);
            Comment savedComment = commentRepository.save(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // cohesion artırmak için yorum oluşturma işlemi burada yapıldı.
    // Bu sayede bir post ile ilgili tüm işlemler tek controllerda toplanmış oldu.

    // Bir yazılımın kalite standartı genel olarak 2 faktöre bağlıdır:
    // Cohesion (İçsel Bağlılık): Bir modülün veya bile
    // Coupling (Bağımlılık): Modüller arasındaki bağımlılıkların azlığı.
    // Düşük coupling, modüllerin birbirinden bağımsız çalışabilmesini sağlar
    // Yüksek cohesion ise bir modülün kendi içinde tutarlı ve odaklanmış olmasını ifade eder.
    // Yüksek cohesion ve düşük coupling, yazılımın bakımını, genişletilmesini ve test edilmesini kolaylaştırır.

}
