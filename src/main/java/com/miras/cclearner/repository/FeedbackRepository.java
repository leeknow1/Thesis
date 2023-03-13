package com.miras.cclearner.repository;

import com.miras.cclearner.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Calendar;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    @Modifying
    @Query("delete from Feedback f where f.createdDate < :date")
    void deleteBy7Days(@Param("date") Calendar calendar);
}
