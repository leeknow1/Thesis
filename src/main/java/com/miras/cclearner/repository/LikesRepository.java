package com.miras.cclearner.repository;

import com.miras.cclearner.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {

    @Query("select l from Likes l where l.userId = :userId and l.charId = :charId and l.isLiked = true")
    Likes isUserLikedChar(@Param("userId") Long userId, @Param("charId") Long charId);

    @Query("select l from Likes l where l.userId = :userId and l.charId = :charId and l.isDisliked = true")
    Likes isUserDislikedChar(@Param("userId") Long userId, @Param("charId") Long charId);
}
