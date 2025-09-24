package com.example.tacoshop.repository;

import com.example.tacoshop.entity.User;
import com.example.tacoshop.entity.type.UserRole;
import com.example.tacoshop.repository.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Page<User> findAllByRoleAndActiveIsTrue(UserRole userRole, Pageable pageable);
}
