package com.smokingcessation.platform.controller;

import com.smokingcessation.platform.entity.MembershipPackage;
import com.smokingcessation.platform.entity.User;
import com.smokingcessation.platform.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/membership")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MembershipController {

    private final MembershipService membershipService;

    // Lấy tất cả gói thành viên đang hoạt động
    @GetMapping("/packages")
    public ResponseEntity<List<MembershipPackage>> getAllActivePackages() {
        List<MembershipPackage> packages = membershipService.getAllActivePackages();
        return ResponseEntity.ok(packages);
    }

    // Lấy chi tiết gói thành viên
    @GetMapping("/packages/{packageId}")
    public ResponseEntity<MembershipPackage> getPackageById(@PathVariable Long packageId) {
        return membershipService.getPackageById(packageId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // Đăng ký gói thành viên
    @PostMapping("/subscribe/{userId}/{packageId}")
    public ResponseEntity<User> subscribeMembership(@PathVariable Long userId, @PathVariable Long packageId) {
        try {
            User user = membershipService.subscribeMembership(userId, packageId);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Gia hạn membership
    @PostMapping("/renew/{userId}")
    public ResponseEntity<User> renewMembership(@PathVariable Long userId) {
        try {
            User user = membershipService.renewMembership(userId);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Hủy membership
    @PostMapping("/cancel/{userId}")
    public ResponseEntity<User> cancelMembership(@PathVariable Long userId) {
        try {
            User user = membershipService.cancelMembership(userId);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Kiểm tra trạng thái membership
    @GetMapping("/status/{userId}")
    public ResponseEntity<Boolean> isActiveMember(@PathVariable Long userId) {
        try {
            boolean isActive = membershipService.isActiveMember(userId);
            return ResponseEntity.ok(isActive);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Lấy thông tin chi tiết membership của user
    @GetMapping("/info/{userId}")
    public ResponseEntity<MembershipService.MembershipInfo> getUserMembershipInfo(@PathVariable Long userId) {
        try {
            MembershipService.MembershipInfo info = membershipService.getUserMembershipInfo(userId);
            return ResponseEntity.ok(info);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Admin: Tạo gói thành viên mới
    @PostMapping("/packages")
    public ResponseEntity<MembershipPackage> createPackage(@RequestBody PackageRequest request) {
        try {
            MembershipPackage membershipPackage = new MembershipPackage();
            membershipPackage.setName(request.getName());
            membershipPackage.setDescription(request.getDescription());
            membershipPackage.setPrice(request.getPrice());
            membershipPackage.setDurationMonths(request.getDurationMonths());
            membershipPackage.setMaxCoachSessions(request.getMaxCoachSessions());
            membershipPackage.setHasPremiumFeatures(request.getHasPremiumFeatures());
            membershipPackage.setHasPersonalizedPlan(request.getHasPersonalizedPlan());
            membershipPackage.setHasProgressAnalytics(request.getHasProgressAnalytics());
            membershipPackage.setStatus(MembershipPackage.PackageStatus.ACTIVE);

            MembershipPackage created = membershipService.createPackage(membershipPackage);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Admin: Cập nhật gói thành viên
    @PutMapping("/packages/{packageId}")
    public ResponseEntity<MembershipPackage> updatePackage(@PathVariable Long packageId,
                                                          @RequestBody PackageRequest request) {
        try {
            MembershipPackage membershipPackage = new MembershipPackage();
            membershipPackage.setId(packageId);
            membershipPackage.setName(request.getName());
            membershipPackage.setDescription(request.getDescription());
            membershipPackage.setPrice(request.getPrice());
            membershipPackage.setDurationMonths(request.getDurationMonths());
            membershipPackage.setMaxCoachSessions(request.getMaxCoachSessions());
            membershipPackage.setHasPremiumFeatures(request.getHasPremiumFeatures());
            membershipPackage.setHasPersonalizedPlan(request.getHasPersonalizedPlan());
            membershipPackage.setHasProgressAnalytics(request.getHasProgressAnalytics());

            MembershipPackage updated = membershipService.updatePackage(membershipPackage);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Admin: Vô hiệu hóa gói thành viên
    @PostMapping("/packages/{packageId}/deactivate")
    public ResponseEntity<Void> deactivatePackage(@PathVariable Long packageId) {
        try {
            membershipService.deactivatePackage(packageId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Admin: Kích hoạt gói thành viên
    @PostMapping("/packages/{packageId}/activate")
    public ResponseEntity<Void> activatePackage(@PathVariable Long packageId) {
        try {
            membershipService.activatePackage(packageId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Admin: Lấy tất cả gói thành viên (bao gồm inactive)
    @GetMapping("/packages/all")
    public ResponseEntity<List<MembershipPackage>> getAllPackages() {
        List<MembershipPackage> packages = membershipService.getAllPackages();
        return ResponseEntity.ok(packages);
    }

    // DTO
    public static class PackageRequest {
        private String name;
        private String description;
        private BigDecimal price;
        private Integer durationMonths;
        private Integer maxCoachSessions;
        private Boolean hasPremiumFeatures;
        private Boolean hasPersonalizedPlan;
        private Boolean hasProgressAnalytics;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public Integer getDurationMonths() { return durationMonths; }
        public void setDurationMonths(Integer durationMonths) { this.durationMonths = durationMonths; }
        public Integer getMaxCoachSessions() { return maxCoachSessions; }
        public void setMaxCoachSessions(Integer maxCoachSessions) { this.maxCoachSessions = maxCoachSessions; }
        public Boolean getHasPremiumFeatures() { return hasPremiumFeatures; }
        public void setHasPremiumFeatures(Boolean hasPremiumFeatures) { this.hasPremiumFeatures = hasPremiumFeatures; }
        public Boolean getHasPersonalizedPlan() { return hasPersonalizedPlan; }
        public void setHasPersonalizedPlan(Boolean hasPersonalizedPlan) { this.hasPersonalizedPlan = hasPersonalizedPlan; }
        public Boolean getHasProgressAnalytics() { return hasProgressAnalytics; }
        public void setHasProgressAnalytics(Boolean hasProgressAnalytics) { this.hasProgressAnalytics = hasProgressAnalytics; }
    }
}
