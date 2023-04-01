package com.miras.cclearner.repository;

import com.miras.cclearner.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    Users findByUsername(String username);

    Users findByEmail(String email);

    Page<Users> findAllByUsername(String username, Pageable pageable);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
