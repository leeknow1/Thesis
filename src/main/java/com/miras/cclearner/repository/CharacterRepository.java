package com.miras.cclearner.repository;

import com.miras.cclearner.entity.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CharacterRepository extends JpaRepository<Character, Long> {

    @Query(value = "select * from public.character as chars inner join public.character_category as cate on cate.category_id = :id and cate.character_id = chars.id and chars.status = 'APPROVED' ", nativeQuery = true)
    List<Character> findAllByCategory(@Param("id") Long id);

    @Query("select c from Character c where c.name like :name and c.status = 'APPROVED'")
    List<Character> findAllByName(@Param("name") String name);

    List<Character> findAllByOriginalId(Long id);

    boolean existsByImageName(String name);

    boolean existsByAudioName(String name);

    boolean existsByVideoName(String name);

    boolean existsByName(String name);

    @Query("select c from Character c where c.status = 'REQUEST' ")
    List<Character> findAllModified();
}
