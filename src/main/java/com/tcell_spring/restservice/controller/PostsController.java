package com.tcell_spring.restservice.controller;
import com.tcell_spring.restservice.entity.Comment;
import com.tcell_spring.restservice.entity.Post;
import com.tcell_spring.restservice.repository.ICommentRepository;
import com.tcell_spring.restservice.repository.IPostRepository;
import com.tcell_spring.restservice.request.comments.CommentCreateRequest;
import com.tcell_spring.restservice.request.posts.PostChangeReleasedRequest;
import com.tcell_spring.restservice.request.posts.PostCreateRequest;
import com.tcell_spring.restservice.request.posts.PostUpdateRequest;
import com.tcell_spring.restservice.response.comments.CommentDetailResponse;
import com.tcell_spring.restservice.response.posts.PostCreateResponse;
import com.tcell_spring.restservice.response.posts.PostDetailResponse;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
    // @Valid anatasyonu ile request objesi üzerinde tanımlanan validation kuralları çalıştırılır.
    @PostMapping
    public ResponseEntity<PostCreateResponse> createPost(@Valid @RequestBody PostCreateRequest request) {

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
    public ResponseEntity<String> updatePost(@PathVariable Integer id, @RequestBody PostUpdateRequest request) {


        Optional<Post> postOptional = postRepository.findById(id);

        // update işlemleri için ekstra veri tutarlılığı kontrolleri yapılabilir.
        if(request.getId() != null && !request.getId().equals(id)) {
            // request body'sindeki id ile path variable'daki id uyuşmuyorsa bad request döneriz.
            // 400 Bad Request Id uyuşmazlığı
            return ResponseEntity.badRequest().body("Id in request body does not match Id in path variable");
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
    public ResponseEntity<String> changeReleaseStatus(@PathVariable Integer id, @RequestBody PostChangeReleasedRequest request) {
        Optional<Post> postOptional = postRepository.findById(id);

        // 400 Bad Request Id uyuşmazlığı
        if(request.getPostId() != null && !request.getPostId().equals(id)) {
            return ResponseEntity.badRequest().body("Id in request body does not match Id in path variable");
        }

        if(postOptional.isPresent()) {
            Post post = postOptional.get();
            post.setIsReleased(request.getIsReleased());
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

        // Eğer böyle bir post yoksa 404 döneriz.
        if(!postRepository.existsById(postId)) {
            return ResponseEntity.notFound().build();
        }


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
    public ResponseEntity<CommentDetailResponse> createPostComment(@PathVariable Integer postId,@RequestBody CommentCreateRequest request) {

        Optional<Post> optionalPost = postRepository.findById(postId);

        if(optionalPost.isPresent()) {

            // request to entity mapping
            Comment entity = new Comment();
            BeanUtils.copyProperties(request,entity);
            Comment savedComment = commentRepository.save(entity);

            // Ekrana kayıt sonrası vereceğimiz response objesi
            CommentDetailResponse commentResponse = new CommentDetailResponse();
            BeanUtils.copyProperties(savedComment, commentResponse);

            return ResponseEntity.status(HttpStatus.CREATED).body(commentResponse);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Not: Test etmek amaçlı yapıldı.
    // api/v1/posts/withComments -> HTTP POST -> Create Post with Comments //

    // Not: CascadeType.PERSIST veya ALL seçili olmasaydı bu özellikten faydalanamazdık.
    @PostMapping("withComments")
    public ResponseEntity<Post> createPostWithComments() {

        Optional<Post> postEntityOptional =  postRepository.findById(1);

        if(postEntityOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Post postEntity = postEntityOptional.get();

        // Yeni bir yorum oluştur
        Comment comment1 = new Comment();
        comment1.setContent("Bu birinci yorumdur.");
        comment1.setPostId(1); // Yorumun hangi post'a ait olduğunu belirt
        comment1.setPost(postEntity);

        // veritabanından findById ile çekilen entity'nin comments listesi dolu geleceği için direkt ekleme yapabiliriz.
        postEntity.getComments().add(comment1);

        Comment comment2 = new Comment();
        comment2.setContent("Bu ikinci yorumdur.");
        comment2.setPostId(1);
        comment1.setPost(postEntity);
        postEntity.getComments().add(comment2);

        // Aggregate Root Post Entity -> Child Entity Comment -> Bu geliştirme yaklaşımı Domain Centric Design (DDD) prensiplerine uygundur.
        // ORM tooları ilişikli nesnelerde işlem yaparken parent-child ilişkisini yönetmek için CascadeType ayarlarını kullanır.
        // Bu örnekte ise Post entity üzerinden Child Entity Comment kayıdı Post Repository ile yapıldı. CommentRepository'e ihtiyaç kalmadı.

        // Aggregate Root olan Post entity'sini kaydet dediğimizde aynı transaction içerisinde bağlı alt nesnelerde (Comment) kaydedilir. CascadeType.PERSIST sayesinde.
        Post response = postRepository.save(postEntity);

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

        // default ilk sayfa 0'dan başlar.
        page = page == 1 ? 0 : page -1; // kullanıcı 1'den başlatmak isterse diye ayarlandı.

        Pageable pageable = !sortDir.equals("desc") ? PageRequest.of(page, size,
                Sort.by(sortBy).ascending()
        ) : PageRequest.of(page, size,
                Sort.by(sortBy).descending());

        Page<PostDetailResponse> postPage = postRepository.findAllByTitleContainsIgnoreCase(title,pageable).map(postEntity -> {
            PostDetailResponse response = new PostDetailResponse();
            BeanUtils.copyProperties(postEntity, response);
            return response;
        });



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
