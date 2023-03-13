package com.miras.cclearner.repository;

import com.miras.cclearner.entity.CharactersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CharacterEntityRepository extends JpaRepository<CharactersEntity, Long> {

    @Query("select c from CharactersEntity c inner join CharCategory cc on cc.categoryId.id = :id and c.id = cc.characterId.id and c.status = 'APPROVED' ")
    List<CharactersEntity> findAllByCategory(@Param("id") Long id);

    boolean existsByImageName(String name);

    boolean existsByAudioName(String name);

    boolean existsByVideoName(String name);

    boolean existsByName(String name);

    @Query("select c from CharactersEntity c where c.status = 'REQUEST' ")
    List<CharactersEntity> findAllModified();
}
