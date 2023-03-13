package com.miras.cclearner.repository;

import com.miras.cclearner.entity.FeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Calendar;

@Repository
public interface FeedbackEntityRepository extends JpaRepository<FeedbackEntity, Long> {

    @Modifying
    @Query("delete from FeedbackEntity f where f.createdDate < :date")
    void deleteBy7Days(@Param("date") Calendar calendar);
}
