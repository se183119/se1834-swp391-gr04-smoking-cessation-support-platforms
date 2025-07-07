package com.team04.smoking_cessation.controller;

import com.team04.smoking_cessation.entity.SessionFile;
import com.team04.smoking_cessation.service.SessionFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/session-files")
@Tag(name = "Session Files", description = "APIs for uploading and listing files in coach sessions")
@SecurityRequirement(name = "bearerAuth")
public class SessionFileController {
    @Autowired
    private SessionFileService sessionFileService;

    @PostMapping("/upload")
    @Operation(summary = "Upload file to a session (coach or member)")
    public ResponseEntity<SessionFile> uploadFile(@RequestBody Map<String, Object> req) {
        Long sessionId = Long.valueOf(req.get("sessionId").toString());
        String fileName = req.get("fileName").toString();
        String fileUrl = req.get("fileUrl").toString(); // Đường dẫn file đã upload lên server
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String uploadedBy = auth.getName();
        SessionFile file = sessionFileService.saveFile(sessionId, fileName, fileUrl, uploadedBy);
        return ResponseEntity.ok(file);
    }

    @GetMapping("/session/{sessionId}")
    @Operation(summary = "Get all files for a session")
    public ResponseEntity<List<SessionFile>> getFilesForSession(@PathVariable Long sessionId) {
        List<SessionFile> files = sessionFileService.getFilesForSession(sessionId);
        return ResponseEntity.ok(files);
    }
} 