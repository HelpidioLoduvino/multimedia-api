package com.example.multimediaapi.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class UploadPath {

    @Value("${music.upload.dir}")
    private String musicUploadDir;

    @Value("${image.upload.dir}")
    private String imageUploadDir;

    @Value("${video.upload.dir}")
    private String videoUploadDir;


}
