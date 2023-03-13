package com.miras.cclearner.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "char_category")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CharCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "char_id")
    private CharactersEntity characterId;

    @OneToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity categoryId;
}
