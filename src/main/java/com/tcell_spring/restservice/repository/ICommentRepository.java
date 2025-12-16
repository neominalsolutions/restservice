package com.tcell_spring.restservice.repository;

import com.tcell_spring.restservice.entity.Comment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICommentRepository extends CrudRepository<Comment, Integer> {

    // extend etmek zorunda kaldık. Query methodları kullanabilmek için isimlendirme kurallarına uymalıyız.
    // findBy + fieldName
    List<Comment> findByPostId(Integer postId);

}

