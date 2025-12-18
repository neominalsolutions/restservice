package com.tcell_spring.restservice.infra.repository;

import com.tcell_spring.restservice.domain.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPostRepository extends JpaRepository<Post, Integer> {


    boolean existsByTitle(String title);

    // title üzerinden arama yapabilmek için query method, aynı zamanda sayfalama desteği de ekledik
    Page<Post> findAllByTitleContainsIgnoreCase(String title, Pageable pageable);

    // JPQL kullanarak released olan ve releaseDate'i şu anki zamandan küçük veya eşit olan postları getiren method
    // Sayfalama işlemleri query methodlarında olduğu gibi Pageable ile yapılır.
    @Query("SELECT p FROM Post p WHERE p.isReleased = true AND p.releaseDate <= CURRENT_TIMESTAMP")
    Page<Post> findAllReleasedPosts(Pageable pageable);




}

