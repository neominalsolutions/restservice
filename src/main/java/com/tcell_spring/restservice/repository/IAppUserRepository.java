package com.tcell_spring.restservice.repository;


import com.tcell_spring.restservice.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAppUserRepository extends JpaRepository<AppUser, String> {

    AppUser findByUsername(String username);
}
