package com.team04.smoking_cessation.service;

import com.team04.smoking_cessation.entity.SessionFile;
import com.team04.smoking_cessation.entity.CoachSession;
import com.team04.smoking_cessation.repository.SessionFileRepository;
import com.team04.smoking_cessation.repository.CoachSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SessionFileService {
    @Autowired
    private SessionFileRepository sessionFileRepository;
    @Autowired
    private CoachSessionRepository coachSessionRepository;

    // Lưu file gửi trong session
    public SessionFile saveFile(Long sessionId, String fileName, String fileUrl, String uploadedBy) {
        CoachSession session = coachSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        SessionFile file = new SessionFile();
        file.setSession(session);
        file.setFileName(fileName);
        file.setFileUrl(fileUrl);
        file.setUploadedBy(uploadedBy);
        file.setUploadedAt(LocalDateTime.now());
        return sessionFileRepository.save(file);
    }

    // Lấy danh sách file của session
    public List<SessionFile> getFilesForSession(Long sessionId) {
        CoachSession session = coachSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        return sessionFileRepository.findBySession(session);
    }
} 