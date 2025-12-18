package com.tcell_spring.restservice.domain.service;

import com.tcell_spring.restservice.domain.entity.Post;
import com.tcell_spring.restservice.domain.exception.SameEntityExistException;
import com.tcell_spring.restservice.infra.repository.IPostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

// Bussiness Logic
// Veri kaynağına işlemek için burada bazı bussiness kurallarını yazarız.
// Servis katmanında veritabanına kontrollü erişim yapılır.

@Service
public class PostService {


    private final IPostRepository postRepository;

    public PostService(IPostRepository postRepository) {
        this.postRepository = postRepository;
    }


    public Post create(Post entity) {
        // Post oluşturma işlemleri
        boolean isExists = this.postRepository.existsByTitle(entity.getTitle());

        if (isExists) {
            throw new SameEntityExistException("Post with the same title already exists.");
        }

        return this.postRepository.save(entity);

    }

    // org.springframework.orm.jpa.JpaSystemException: A collection with orphan deletion was no longer referenced by the owning entity instance: com.tcell_spring.restservice.domain.entity.Post.comments -> Hatası var.
    public void update(Post entity) {

       Optional<Post> post =  this.postRepository.findById(entity.getId());

       if(post.isPresent()){
           Post data = post.get();
           BeanUtils.copyProperties(entity, data, "createdAt", "isReleased","releaseDate");
           this.postRepository.save(data);
       } else {
           throw new EntityNotFoundException("Post not found.");
       }

    }

    public void delete(Integer id) {

        Optional<Post> post =  this.postRepository.findById(id);

        if(post.isPresent()){
            this.postRepository.delete(post.get());
        } else {
            throw new EntityNotFoundException("Post not found.");
        }


    }

    // Update işlemlerinde hata var
    // org.springframework.orm.jpa.JpaSystemException: A collection with orphan deletion was no longer referenced by the owning entity instance: com.tcell_spring.restservice.domain.entity.Post.comments
    public void changeReleaseStatus(Integer id, Boolean status) {

        Optional<Post> post =  this.postRepository.findById(id);

        if(post.isPresent()){



            post.get().setIsReleased(status);
            this.postRepository.save(post.get());
        } else {
            throw new EntityNotFoundException("Post not found.");
        }
    }


}
