package com.application.imagerepo.image;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findImagesByUser_Id(Long id);

    List<Image> findImagesByAccessType(String accessType);
}
