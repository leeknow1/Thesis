package com.miras.cclearner.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "character")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "img_name")
    private String imageName;

    @Column(name = "vid_name")
    private String videoName;

    @Column(name = "aud_name")
    private String audioName;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "example")
    private String example;

    @Column(name = "updated")
    private String updatedDate;

    @Column(name = "author")
    private String author;

    @Column(name = "status")
    private String status;

    @Column(name = "likes")
    private Integer likes = 0;

    @Column(name = "dislikes")
    private Integer dislikes = 0;

    @Column(name = "original_id")
    private Long originalId;

    @ManyToMany
    private Set<Category> category;
}
