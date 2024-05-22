package com.example.multimediaapi.repository;

import com.example.multimediaapi.dto.UserDto;
import com.example.multimediaapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    UserDetails findByEmail(String email);

    @Query("select new com.example.multimediaapi.dto.UserDto(u.id, u.name, u.surname, u.email, u.userRole, u.createdAt) from User u")
    List<UserDto> findAllUsers();

    @Query("select new com.example.multimediaapi.model.User(u.id, u.name, u.surname, u.email, u.password, u.userRole, u.createdAt) from User u where u.email = :email")
    User findByUserEmail(String email);

    @Query("select new com.example.multimediaapi.dto.UserDto(u.id, u.name, u.surname, u.email, u.userRole, u.createdAt) from User u where u.userRole = 'CLIENT'")
    List<UserDto> findAllUsersByRoleClient();
}
