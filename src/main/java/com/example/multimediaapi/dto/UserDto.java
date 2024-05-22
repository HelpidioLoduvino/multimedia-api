package com.example.multimediaapi.dto;

import java.util.Date;

public record UserDto(Long id, String name, String surname, String email, String userRole, Date createdAt) {
}
