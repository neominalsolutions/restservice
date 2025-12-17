package com.tcell_spring.restservice.repository;

import com.tcell_spring.restservice.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPostRepository extends JpaRepository<Post, Integer> {

    // title üzerinden arama yapabilmek için query method, aynı zamanda sayfalama desteği de ekledik
    Page<Post> findAllByTitleContainsIgnoreCase(String title, Pageable pageable);

}

