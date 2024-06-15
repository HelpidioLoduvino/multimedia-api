package com.example.multimediaapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlbumReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating;

    private double media = 0;

    @Column(columnDefinition = "LONGTEXT")
    private String overview;

    @ManyToOne
    @JoinColumn(name = "album_id")
    private MusicRelease musicRelease;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
