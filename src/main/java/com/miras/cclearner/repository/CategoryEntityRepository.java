package com.miras.cclearner.repository;

import com.miras.cclearner.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryEntityRepository extends JpaRepository<CategoryEntity, Long> {

    @Query("select distinct cc from CategoryEntity cc inner join CharCategory cr on cc.id = cr.categoryId.id order by cc.id asc")
    List<CategoryEntity> findCategoryByRequests();

    boolean existsByName(String name);
}
