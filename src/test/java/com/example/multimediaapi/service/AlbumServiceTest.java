package com.example.multimediaapi.service;

import com.example.multimediaapi.repository.AlbumReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @InjectMocks
    private AlbumService albumService;

    @Mock
    private AlbumReviewRepository albumReviewRepository;

    @Test
    public void getAlbumReviewOverall() {
        Long albumId = 1L;
        List<Double> medias = Arrays.asList(4.5, 3.0, 5.0);
        when(albumReviewRepository.findMediaByMusicRelease_id(albumId)).thenReturn(medias);

        Double result = albumService.getAlbumReviewOverall(albumId);

        assertNotNull(result);
        assertEquals(4.5, result);
    }
}