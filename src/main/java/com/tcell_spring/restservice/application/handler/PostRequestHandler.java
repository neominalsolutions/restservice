package com.tcell_spring.restservice.application.handler;

import com.tcell_spring.restservice.application.request.comments.CommentCreateRequest;
import com.tcell_spring.restservice.application.request.posts.PostChangeReleasedRequest;
import com.tcell_spring.restservice.application.request.posts.PostCreateRequest;
import com.tcell_spring.restservice.application.request.posts.PostUpdateRequest;
import com.tcell_spring.restservice.application.response.comments.CommentDetailResponse;
import com.tcell_spring.restservice.application.response.posts.PostCreateResponse;
import com.tcell_spring.restservice.application.response.posts.PostDetailResponse;
import com.tcell_spring.restservice.domain.entity.Comment;
import com.tcell_spring.restservice.domain.entity.Post;
import com.tcell_spring.restservice.domain.service.PostService;
import com.tcell_spring.restservice.infra.repository.ICommentRepository;
import com.tcell_spring.restservice.infra.repository.IPostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// Burada makale isteklerini işlemek için gerekli metodlar tanımlanmıştır. User story'lerde belirtilen işlevsellikler için bu metodlar kullanılacaktır.
// Use-case'ler bu handler sınıfını kullanarak iş mantığını uygulayabilirler.


@Component
public class PostRequestHandler {

    private final PostService postService; // İş mantığını içeren servis
    private final IPostRepository postRepository; // Veri erişim katmanı için repository
    private final ICommentRepository commentRepository;

    public PostRequestHandler(PostService postService, IPostRepository postRepository, ICommentRepository commentRepository) {
        this.postService = postService;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }


    public List<PostDetailResponse> getAllPosts() {
        return postRepository.findAll().stream().map(postEntity ->
                new PostDetailResponse(
                        postEntity.getId(),
                        postEntity.getTitle(),
                        postEntity.getContent(),
                        postEntity.getIsReleased(),
                        postEntity.getReleaseDate(),
                        null // yorumlar eklenmedi -> Liste ekranları Data Grid üzerinde gösterilir detay gösterecek kadar alan yoktur. Fetch performansı için yorumlar eklenmedi. Tek bir sorgu ile sadece Post verileri çekildi.
                )).toList();
    }

    public PostDetailResponse getPostById(Integer postId) {
        Optional<Post> postOptional =  postRepository.findById(postId);

        if(postOptional.isPresent()) {
            PostDetailResponse response = new PostDetailResponse();
            // Response objesine entity'deki verileri kopyala
            BeanUtils.copyProperties(postOptional.get(), response);
            // modelmapper yerine spring'in kendi BeanUtils kütüphanesi kullanıldı. Performans açısından daha iyi sonuç verir.

            List<CommentDetailResponse> commentResponse = postOptional.get().getComments().stream().map(commentEntity -> {
                CommentDetailResponse commentDto = new CommentDetailResponse();
                BeanUtils.copyProperties(commentEntity, commentDto);
                return commentDto;
            }).toList();

            response.setComments(commentResponse);

            return response;

        } else {
            throw new EntityNotFoundException("Post not found with id: " + postId);
        }
    }

    public PostCreateResponse createPost(PostCreateRequest request) {

        Post postEntity = new Post();
        BeanUtils.copyProperties(request,postEntity);  // request'ten entity'e verileri kopyala
        Post entity = postService.create(postEntity);

        return  new PostCreateResponse(entity.getId(), LocalDateTime.now());
    }

    public void updatePost(Integer postId,PostUpdateRequest request) throws BadRequestException {


        // update işlemleri için ekstra veri tutarlılığı kontrolleri yapılabilir.
        if(request.getId() != null && !request.getId().equals(postId)) {
           throw new BadRequestException("id mismatch");
        }


        Post entity = new Post();
        BeanUtils.copyProperties(request,entity);  // request'ten entity'e verileri k
        postService.update(entity);
    }

    public void deletePost(Integer postId) {
        postService.delete(postId);
    }

    public List<CommentDetailResponse> getCommentsByPostId(Integer postId) {

        return commentRepository.findByPostId(postId).stream().map(commentEntity ->{
            CommentDetailResponse commentResponse = new CommentDetailResponse();
            BeanUtils.copyProperties(commentEntity, commentResponse);
            return commentResponse;
        }).toList();

    }

    public CommentDetailResponse addCommentToPost(Integer postId, CommentCreateRequest request) {

        Optional<Post> optionalPost = postRepository.findById(postId);

        if(optionalPost.isPresent()) {

            // request to entity mapping
            Comment entity = new Comment();
            BeanUtils.copyProperties(request,entity);
            Comment savedComment = commentRepository.save(entity);

            // Ekrana kayıt sonrası vereceğimiz response objesi
            CommentDetailResponse commentResponse = new CommentDetailResponse();
            BeanUtils.copyProperties(savedComment, commentResponse);

            return commentResponse;
        } else {
            throw new EntityNotFoundException("Post not found with id: " + postId);
        }

    }

    public Post createPostWithComments() {

        Optional<Post> postEntityOptional =  postRepository.findById(1);

        if(postEntityOptional.isEmpty()) {
            throw new EntityNotFoundException("Post not found with id: " + 1);
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

        return response;
    }

    public void changePostReleaseStatus(Integer postId, PostChangeReleasedRequest request) {
        postService.changeReleaseStatus(postId, request.getIsReleased());
    }

    // Page<Post> postPage = postRepository.findAll(org.springframework.data.domain.PageRequest.of(page, size));
    public Page<PostDetailResponse> getPagedPosts(int page, int size, String sortBy, String sortDir, String title) {


        page = page == 1 ? 0 : page -1; // kullanıcı 1'den başlatmak isterse diye ayarlandı.

        Pageable pageable = !sortDir.equals("desc") ? PageRequest.of(page, size,
                Sort.by(sortBy).ascending()
        ) : PageRequest.of(page, size,
                Sort.by(sortBy).descending());

        return postRepository.findAllByTitleContainsIgnoreCase(title,pageable).map(postEntity -> {
            PostDetailResponse response = new PostDetailResponse();
            BeanUtils.copyProperties(postEntity, response);
            return response;
        });
    }

}
