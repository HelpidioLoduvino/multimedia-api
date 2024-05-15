package com.example.multimediaapi.dto;

import java.util.Date;

public record UserDTO(Long id, String name, String surname, String email, String userRole, boolean editor, Date createdAt) {
}
