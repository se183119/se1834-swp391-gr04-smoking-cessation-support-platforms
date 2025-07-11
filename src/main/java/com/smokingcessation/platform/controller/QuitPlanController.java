package com.smokingcessation.platform.controller;

import com.smokingcessation.platform.dto.CreatePlanRequest;
import com.smokingcessation.platform.dto.PlanResponse;
import com.smokingcessation.platform.dto.ProgressRequest;
import com.smokingcessation.platform.dto.ProgressResponse;
import com.smokingcessation.platform.entity.QuitPlan;
import com.smokingcessation.platform.entity.User;
import com.smokingcessation.platform.service.QuitPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/quit-plans")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Quit Plan Management", description = "APIs for creating and managing smoking cessation plans")
public class QuitPlanController {

    private final QuitPlanService quitPlanService;

    // Tạo kế hoạch cai thuốc thủ công
    @PostMapping
    public ResponseEntity<PlanResponse> createPlan(@RequestBody CreatePlanRequest req) {
        PlanResponse resp = quitPlanService.createPlan(req);
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

    @GetMapping("/{planId}")
    public ResponseEntity<PlanResponse> getPlan(@PathVariable Long planId) {
        PlanResponse resp = quitPlanService.getPlanById(planId);
        if (resp == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resp);
    }



    @PostMapping("/{planId}/progress")
    public ResponseEntity<ProgressResponse> upsertProgress(
            @PathVariable Long planId,
            @RequestBody ProgressRequest req) {
        ProgressResponse resp = quitPlanService.upsertProgress(planId, req);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{planId}/progress")
    public ResponseEntity<List<ProgressResponse>> getProgressMonth(
            @PathVariable Long planId,
            @RequestParam int year,
            @RequestParam int month) {
        YearMonth ym = YearMonth.of(year, month);
        List<ProgressResponse> list = quitPlanService.getProgressInMonth(planId, ym);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/get-by-user/{userId}")
    public ResponseEntity<List<PlanResponse>> getPlansByUser(
            @PathVariable Long userId) {
        List<PlanResponse> plans = quitPlanService.getByUser(userId);
        return ResponseEntity.ok(plans);
    }
}
