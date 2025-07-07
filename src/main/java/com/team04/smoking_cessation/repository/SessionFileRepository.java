package com.team04.smoking_cessation.repository;

import com.team04.smoking_cessation.entity.SessionFile;
import com.team04.smoking_cessation.entity.CoachSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionFileRepository extends JpaRepository<SessionFile, Long> {
    List<SessionFile> findBySession(CoachSession session);
} 