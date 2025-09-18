package com.example.shop.repository;

import com.example.shop.model.type.UserRole;
import com.example.shop.model.users.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByUsername(String username);

    List<UserAccount> findAllByUserRole(UserRole role);
}