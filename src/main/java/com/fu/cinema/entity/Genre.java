package com.fu.cinema.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "genres")
@Getter
@Setter
@NoArgsConstructor
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long genreId;

    @Column(nullable = false, unique = true, length = 50)
    private String genreName;

    @Column(nullable = false, length = 500)
    private String description;

    @OneToMany(mappedBy = "genre")
    private List<Movie> movies = new ArrayList<>();

    public Genre(String genreName, String description) {
        this.genreName = genreName;
        this.description = description;
    }
}
