package com.tcell_spring.restservice.infra.repository;

import com.tcell_spring.restservice.domain.entity.Comment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICommentRepository extends CrudRepository<Comment, Integer> {

    // extend etmek zorunda kaldık. Query methodları kullanabilmek için isimlendirme kurallarına uymalıyız.
    // findBy + fieldName
    List<Comment> findByPostId(Integer postId);

    // Join fetch örneği
    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.post WHERE c.postId = :postId")
    List<Comment> findCommentsByPostId(Integer postId);

}

