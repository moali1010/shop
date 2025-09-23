package com.example.tacoshop.repository.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {

    Optional<T> findByIdAndActiveIsTrue(ID id);

    Page<T> findAllByActiveIsTrue(Pageable pageable);
}
