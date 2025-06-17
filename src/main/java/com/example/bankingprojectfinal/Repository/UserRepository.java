package com.example.bankingprojectfinal.Repository;

import com.example.bankingprojectfinal.Model.Entity.UserEntity;
import com.example.bankingprojectfinal.Model.Enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    Page<UserEntity> findByStatus(UserStatus status, Pageable pageable);
    Optional<UserEntity> findByUsername(String username);
}