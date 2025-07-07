package com.team04.smoking_cessation.repository;

import com.team04.smoking_cessation.entity.DailyLog;
import com.team04.smoking_cessation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {

    Optional<DailyLog> findByUserAndLogDate(User user, LocalDate logDate);

    List<DailyLog> findByUserOrderByLogDateDesc(User user);

    List<DailyLog> findByUserAndLogDateBetween(User user, LocalDate startDate, LocalDate endDate);

    @Query("SELECT COUNT(dl) FROM DailyLog dl WHERE dl.user = :user AND dl.isSmokeFree = true")
    Long countSmokeFreeDaysByUser(@Param("user") User user);

    @Query("SELECT dl FROM DailyLog dl WHERE dl.user = :user AND dl.isSmokeFree = true ORDER BY dl.logDate DESC")
    List<DailyLog> findSmokeFreeDaysByUser(@Param("user") User user);

    @Query("SELECT AVG(dl.cigarettesSmoked) FROM DailyLog dl WHERE dl.user = :user AND dl.logDate BETWEEN :startDate AND :endDate")
    Double getAverageCigarettesPerDay(@Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(DISTINCT dl.user) FROM DailyLog dl WHERE dl.isSmokeFree = true")
    Long countUsersWithSmokeFreeDays();
}
