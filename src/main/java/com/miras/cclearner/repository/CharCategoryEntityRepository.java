package com.miras.cclearner.repository;

import com.miras.cclearner.entity.CharCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CharCategoryEntityRepository extends JpaRepository<CharCategory, Long> {
}
