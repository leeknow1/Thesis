package com.miras.cclearner.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "likes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Likes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "char_id")
    private Long charId;

    @Column(name = "is_liked", nullable = false)
    private boolean isLiked;

    @Column(name = "is_disliked", nullable = false)
    private boolean isDisliked;
}
