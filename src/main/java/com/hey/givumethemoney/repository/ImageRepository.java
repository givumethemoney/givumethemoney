package com.hey.givumethemoney.repository;

import com.hey.givumethemoney.domain.Image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    @Override
    Optional<Image> findById(Long id);  // 이미지 ID로 조회하는 메서드

    @Override
    <S extends Image> S save(S entity);
}
