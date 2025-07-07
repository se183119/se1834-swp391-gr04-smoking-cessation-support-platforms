package com.team04.smoking_cessation.repository;

import com.team04.smoking_cessation.entity.SmokingProfile;
import com.team04.smoking_cessation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SmokingProfileRepository extends JpaRepository<SmokingProfile, Long> {

    Optional<SmokingProfile> findByUserAndIsActiveTrue(User user);

    List<SmokingProfile> findByUserOrderByCreatedAtDesc(User user);

    @Query("SELECT AVG(sp.cigarettesPerDay) FROM SmokingProfile sp WHERE sp.isActive = true")
    Double getAverageCigarettesPerDay();

    @Query("SELECT sp FROM SmokingProfile sp WHERE sp.cigarettesPerDay >= :minCigarettes AND sp.cigarettesPerDay <= :maxCigarettes AND sp.isActive = true")
    List<SmokingProfile> findBySmokingRange(@Param("minCigarettes") Integer minCigarettes, @Param("maxCigarettes") Integer maxCigarettes);
}
