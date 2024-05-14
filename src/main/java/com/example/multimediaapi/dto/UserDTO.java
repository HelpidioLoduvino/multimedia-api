package com.example.multimediaapi.dto;

import java.util.Date;

public record UserDTO(String name, String surname, String email, String userRole, Date createdAt) {
}
