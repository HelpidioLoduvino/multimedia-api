package com.example.multimediaapi.repository;

import com.example.multimediaapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    UserDetails loadUserByUsername(String email);
}
