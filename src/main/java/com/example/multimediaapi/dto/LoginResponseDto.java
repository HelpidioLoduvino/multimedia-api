package com.example.multimediaapi.dto;

public record LoginResponseDto(Long id, String token, String refreshToken, String email, String userRole) {
}
