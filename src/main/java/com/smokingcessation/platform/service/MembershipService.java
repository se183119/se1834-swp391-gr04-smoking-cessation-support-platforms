package com.smokingcessation.platform.service;

import com.smokingcessation.platform.entity.MembershipPackage;
import com.smokingcessation.platform.entity.User;
import com.smokingcessation.platform.repository.MembershipPackageRepository;
import com.smokingcessation.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MembershipService {

    private final MembershipPackageRepository membershipPackageRepository;
    private final UserRepository userRepository;

    public List<MembershipPackage> getAllActivePackages() {
        return membershipPackageRepository.findByStatusOrderByPriceAsc(MembershipPackage.PackageStatus.ACTIVE);
    }

    public Optional<MembershipPackage> getPackageById(Long packageId) {
        return membershipPackageRepository.findById(packageId);
    }

    // Đăng ký gói thành viên cho user
    public User subscribeMembership(Long userId, Long packageId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        MembershipPackage membershipPackage = membershipPackageRepository.findById(packageId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy gói thành viên"));

        if (membershipPackage.getStatus() != MembershipPackage.PackageStatus.ACTIVE) {
            throw new RuntimeException("Gói thành viên này hiện không khả dụng");
        }

        // Cập nhật thông tin membership cho user
        user.setMembershipPackage(membershipPackage);
        user.setMembershipExpiry(LocalDateTime.now().plusMonths(membershipPackage.getDurationMonths()));

        return userRepository.save(user);
    }

    // Gia hạn membership
    public User renewMembership(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (user.getMembershipPackage() == null) {
            throw new RuntimeException("Người dùng chưa có gói thành viên");
        }

        MembershipPackage currentPackage = user.getMembershipPackage();

        // Gia hạn từ thời điểm hiện tại hoặc từ ngày hết hạn (nếu chưa hết hạn)
        LocalDateTime newExpiry;
        if (user.getMembershipExpiry() != null && user.getMembershipExpiry().isAfter(LocalDateTime.now())) {
            newExpiry = user.getMembershipExpiry().plusMonths(currentPackage.getDurationMonths());
        } else {
            newExpiry = LocalDateTime.now().plusMonths(currentPackage.getDurationMonths());
        }

        user.setMembershipExpiry(newExpiry);

        return userRepository.save(user);
    }

    // Hủy membership
    public User cancelMembership(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        user.setMembershipPackage(null);
        user.setMembershipExpiry(null);

        return userRepository.save(user);
    }

    // Kiểm tra membership có còn hiệu lực không
    public boolean isActiveMember(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        return user.getMembershipPackage() != null &&
               user.getMembershipExpiry() != null &&
               user.getMembershipExpiry().isAfter(LocalDateTime.now());
    }

    // Lấy thông tin membership của user
    public MembershipInfo getUserMembershipInfo(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (user.getMembershipPackage() == null) {
            return new MembershipInfo(false, null, null, 0, false, false, false);
        }

        MembershipPackage pkg = user.getMembershipPackage();
        boolean isActive = user.getMembershipExpiry() != null &&
                          user.getMembershipExpiry().isAfter(LocalDateTime.now());

        return new MembershipInfo(
            isActive,
            pkg.getName(),
            user.getMembershipExpiry(),
            pkg.getMaxCoachSessions() != null ? pkg.getMaxCoachSessions() : 0,
            pkg.getHasPremiumFeatures(),
            pkg.getHasPersonalizedPlan(),
            pkg.getHasProgressAnalytics()
        );
    }

    // Quản lý gói thành viên (Admin)
    public MembershipPackage createPackage(MembershipPackage membershipPackage) {
        return membershipPackageRepository.save(membershipPackage);
    }

    public MembershipPackage updatePackage(MembershipPackage membershipPackage) {
        return membershipPackageRepository.save(membershipPackage);
    }

    public void deactivatePackage(Long packageId) {
        MembershipPackage pkg = membershipPackageRepository.findById(packageId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy gói thành viên"));

        pkg.setStatus(MembershipPackage.PackageStatus.INACTIVE);
        membershipPackageRepository.save(pkg);
    }

    public void activatePackage(Long packageId) {
        MembershipPackage pkg = membershipPackageRepository.findById(packageId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy gói thành viên"));

        pkg.setStatus(MembershipPackage.PackageStatus.ACTIVE);
        membershipPackageRepository.save(pkg);
    }

    // Lấy danh sách tất cả membership (bao gồm inactive)
    public List<MembershipPackage> getAllPackages() {
        return membershipPackageRepository.findAll();
    }

    public static class MembershipInfo {
        private final boolean isActive;
        private final String packageName;
        private final LocalDateTime expiryDate;
        private final int maxCoachSessions;
        private final boolean hasPremiumFeatures;
        private final boolean hasPersonalizedPlan;
        private final boolean hasProgressAnalytics;

        public MembershipInfo(boolean isActive, String packageName, LocalDateTime expiryDate,
                            int maxCoachSessions, boolean hasPremiumFeatures,
                            boolean hasPersonalizedPlan, boolean hasProgressAnalytics) {
            this.isActive = isActive;
            this.packageName = packageName;
            this.expiryDate = expiryDate;
            this.maxCoachSessions = maxCoachSessions;
            this.hasPremiumFeatures = hasPremiumFeatures;
            this.hasPersonalizedPlan = hasPersonalizedPlan;
            this.hasProgressAnalytics = hasProgressAnalytics;
        }

        // Getters
        public boolean isActive() { return isActive; }
        public String getPackageName() { return packageName; }
        public LocalDateTime getExpiryDate() { return expiryDate; }
        public int getMaxCoachSessions() { return maxCoachSessions; }
        public boolean isHasPremiumFeatures() { return hasPremiumFeatures; }
        public boolean isHasPersonalizedPlan() { return hasPersonalizedPlan; }
        public boolean isHasProgressAnalytics() { return hasProgressAnalytics; }
    }
}
