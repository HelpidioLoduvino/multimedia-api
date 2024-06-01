package com.example.multimediaapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.sound.midi.Track;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String playlistName;

    private String status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
}
