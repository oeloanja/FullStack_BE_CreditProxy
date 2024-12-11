package com.billit.credit.repository;

import com.billit.credit.entity.MyData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MyDataRepository extends JpaRepository<MyData, Long> {
    Optional<MyData> findByUserId(UUID userId);
}
