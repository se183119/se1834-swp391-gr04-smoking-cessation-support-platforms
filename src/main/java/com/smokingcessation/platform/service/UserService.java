package com.smokingcessation.platform.service;

import com.smokingcessation.platform.dto.SavingsDTO;
import com.smokingcessation.platform.entity.*;
import com.smokingcessation.platform.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private QuitPlanRepository quitPlanRepo;
    @Autowired
    private PlanMilestoneRepository milestoneRepo;
    @Autowired
    private UserProgressRepository progressRepo;

    @Autowired
    private SmokingStatusService smokingStatusService;



    public User registerUser(User user, Set<Role.RoleName> roleNames) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setProfileImage("https://img.freepik.com/premium-vector/character-avatar-isolated_729149-194801.jpg?semt=ais_hybrid&w=740");
        Set<Role> roles = new HashSet<>();
        for (Role.RoleName roleName : roleNames) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role không tồn tại: " + roleName));
            roles.add(role);
        }
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public User loginUser(String username, String password) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user với username: " + username));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Mật khẩu không đúng");
        }

        return user;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public List<User> findCoaches() {
        return userRepository.findAllCoaches();
    }

    public List<User> findActiveMembers() {
        return userRepository.findActiveMembers();
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User updateUserStatus(Long userId, User.UserStatus status) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
        user.setStatus(status);
        return userRepository.save(user);
    }

    public User updateProfile(Long userId, String fullName, String phone, User.Gender gender, Integer age) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        user.setFullName(fullName);
        user.setPhone(phone);
        user.setGender(gender);
        user.setAge(age);

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public SavingsDTO calculateSavings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng với ID: " + userId));

        QuitPlan existingQuitPlan = quitPlanRepo.findByUserAndIsDoneFalse(user);
        if (existingQuitPlan == null) {
            throw new EntityNotFoundException("Không tìm thấy kế hoạch bỏ thuốc lá cho người dùng với ID: " + userId);
        }

        SmokingStatus smokingStatus = smokingStatusService.findCurrentSmokingStatus(userId);

        if (smokingStatus == null) {
            throw new EntityNotFoundException("Không tìm thấy thông tin hút thuốc cho người dùng với ID: " + userId);
        }

        BigDecimal priceOneCigarette = smokingStatus.getCigarettePrice()
                .divide(BigDecimal.valueOf(20), 2, RoundingMode.HALF_UP);

        List<PlanMilestone> milestone = milestoneRepo.findByQuitPlanIdOrderByStepIndex(existingQuitPlan.getId());
        List<UserProgress> progress = progressRepo.findAllByQuitPlanId(existingQuitPlan.getId());
        int totalSmoked = progress.stream()
                .mapToInt(UserProgress::getSmoked)
                .sum();

        LocalDate startDate = existingQuitPlan.getStartDate();
        int totalTarget = 0;
        for (UserProgress item : progress) {
            LocalDate logDate = item.getLogDate();
            // dayOffset = số ngày từ startDate đến ngày log
            int dayOffset = (int) ChronoUnit.DAYS.between(startDate, logDate);
            int targetForThatDay = getTargetForDayOffset(dayOffset, milestone);
            totalTarget += targetForThatDay;
        }

            int totalSmokedSaved = totalTarget - totalSmoked;
        if (totalSmokedSaved < 0) {
            totalSmokedSaved = 0; // Không thể có số thuốc lá đã hút vượt quá mục tiêu
        }

        BigDecimal moneySaved = BigDecimal.valueOf(totalSmokedSaved).multiply(priceOneCigarette);
        BigDecimal hoursSaved = BigDecimal.valueOf(totalSmokedSaved)
                .multiply(BigDecimal.valueOf(5)) // Giả sử mỗi điếu thuốc tiết kiệm 5 phút
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

        BigDecimal minutesSaved = BigDecimal.valueOf(totalSmokedSaved)
                .multiply(BigDecimal.valueOf(5)); // Giả sử mỗi điếu thuốc tiết kiệm 5 phút

        return new SavingsDTO(
                totalSmokedSaved,
                moneySaved,
                hoursSaved,
                minutesSaved
        );

    }

    private int getTargetForDayOffset(int dayOffset, List<PlanMilestone> milestones) {
        if (milestones.isEmpty()) {
            return 0;
        }
        // lướt qua từng mốc, ngày ≤ mốc nào thì lấy luôn target của mốc đó
        for (PlanMilestone m : milestones) {
            if (dayOffset <= m.getDayOffset()) {
                return m.getTargetCigarettes();
            }
        }
        // nếu vượt mốc cuối cùng thì lấy target của mốc cuối
        return milestones.get(milestones.size() - 1).getTargetCigarettes();
    }


    public List<UserProgress> getUserProgressByUserId(Long userId) {
        QuitPlan currentPlan = quitPlanRepo.findByUserAndIsDoneFalse(
                userRepository.findById(userId)
                        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng với ID: " + userId))
        );
        if (currentPlan == null) {
            throw new EntityNotFoundException("Không tìm thấy kế hoạch bỏ thuốc lá cho người dùng với ID: " + userId);
        }
        List<UserProgress> progressList = progressRepo.findAllByQuitPlanId(currentPlan.getId());
        if (progressList.isEmpty()) {
            throw new EntityNotFoundException("Không tìm thấy tiến trình nào cho kế hoạch bỏ thuốc lá của người dùng với ID: " + userId);
        }
        return progressList.stream()
                .sorted(Comparator.comparing(UserProgress::getLogDate))
                .collect(Collectors.toList());
    }

}
