package com.bni.bni.repository;

import com.bni.bni.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    // Mengubah ini untuk mengacu pada ID dari objek User yang terkait
    Optional<Profile> findByUser_Id(Long userId); // Menggunakan user_Id
    boolean existsByUser_Id(Long userId); // Menggunakan user_Id
}