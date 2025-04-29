package com.jwt.demo.repository.user;

import com.jwt.demo.data.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    public Optional<UserEntity> findByUserIdAndPassword(String userId, String password);
}
