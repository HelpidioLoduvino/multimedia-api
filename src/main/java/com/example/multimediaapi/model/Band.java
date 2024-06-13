package com.example.multimediaapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Band {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String bandName;
    @Column(columnDefinition = "LONGTEXT")
    private String history;
    private Integer start;
    private Integer end;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
