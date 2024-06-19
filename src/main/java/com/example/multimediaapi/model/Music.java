package com.example.multimediaapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Music extends Content{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "LONGTEXT")
    private String lyric;

    @ManyToOne
    private Category genre;

    @ManyToOne
    private Album album;

    @ManyToOne
    private MusicRelease musicRelease;

    @ManyToMany
    private List<SongWriter> songwriters;
}
