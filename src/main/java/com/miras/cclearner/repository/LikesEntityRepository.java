package com.miras.cclearner.repository;

import com.miras.cclearner.entity.LikesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LikesEntityRepository extends JpaRepository<LikesEntity, Long> {

    @Query("select l from LikesEntity l where l.userId = :userId and l.charId = :charId and l.isLiked = true")
    LikesEntity isUserLikedChar(@Param("userId") Long userId, @Param("charId") Long charId);

    @Query("select l from LikesEntity l where l.userId = :userId and l.charId = :charId and l.isDisliked = true")
    LikesEntity isUserDislikedChar(@Param("userId") Long userId, @Param("charId") Long charId);
}
