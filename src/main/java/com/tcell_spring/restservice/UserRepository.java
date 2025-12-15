package com.tcell_spring.restservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class UserRepository {
    public String getUserByName(String name) {
      log.info("Saving user " + name);
      return "user " + name;
    }
}
