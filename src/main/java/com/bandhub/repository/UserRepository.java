package com.bandhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bandhub.model.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity,Long> {

    boolean existsByEmail(String email);

}
