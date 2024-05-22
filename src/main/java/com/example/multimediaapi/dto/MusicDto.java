package com.example.multimediaapi.dto;

import com.example.multimediaapi.model.*;

import java.util.List;

public record MusicDto(
        Music music,
        MusicRelease musicRelease
) {
}
