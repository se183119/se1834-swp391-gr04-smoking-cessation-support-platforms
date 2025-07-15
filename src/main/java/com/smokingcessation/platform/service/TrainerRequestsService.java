package com.smokingcessation.platform.service;


import com.smokingcessation.platform.config.EmailService;
import com.smokingcessation.platform.dto.TrainerRequestsDTO;
import com.smokingcessation.platform.entity.Role;
import com.smokingcessation.platform.entity.TrainerRequestsModel;
import com.smokingcessation.platform.entity.User;
import com.smokingcessation.platform.enums.TrainerRequestStatus;
import com.smokingcessation.platform.repository.ITrainerRequestsRepository;
import com.smokingcessation.platform.repository.RoleRepository;
import com.smokingcessation.platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TrainerRequestsService {

    @Autowired
    ITrainerRequestsRepository trainerRequestsRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RoleRepository roleRepository;


    public TrainerRequestsModel createTrainerRequest(TrainerRequestsDTO dto) {

        // check coi user đã có gửi đơn chưa
        var existingRequest = trainerRequestsRepository
                .findByUserIdAndStatus(dto.getUserId(), TrainerRequestStatus.PENDING);

        User userModel = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại."));

        if (existingRequest != null) {
            throw new RuntimeException("Bạn đã gửi yêu cầu trước đó và đang chờ duyệt.");
        }

        TrainerRequestsModel request = new TrainerRequestsModel();
        request.setUser(userModel);
        request.setCertification(dto.getCertification());
        request.setBio(dto.getBio());
        request.setYoe(dto.getYoe());
        request.setStatus(TrainerRequestStatus.PENDING);

        return trainerRequestsRepository.save(request);

    }

    public List<TrainerRequestsModel> findAllByStatus(TrainerRequestStatus status) {
        return trainerRequestsRepository.findAllByStatus(status);
    }

    public List<TrainerRequestsModel> findAllByUserId(Long userId) {
        return trainerRequestsRepository.findAllByUserId(userId);
    }

    public TrainerRequestsModel changeStatusTrainerRequest(Long requestId, TrainerRequestStatus status) {
        // Lấy yêu cầu trở thành huấn luyện viên
        TrainerRequestsModel request = trainerRequestsRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Yêu cầu không tồn tại."));

        // Cập nhật trạng thái
        request.setStatus(status);
        request.setUpdatedAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));

        if (status == TrainerRequestStatus.ACCEPTED) {
            // Gán role COACH cho người gửi yêu cầu
            User trainer = request.getUser();

            Role coachRole = roleRepository.findByName(Role.RoleName.COACH)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy role COACH."));

            Set<Role> updatedRoles = new HashSet<>(trainer.getRoles());
            updatedRoles.add(coachRole);
            trainer.setRoles(updatedRoles);

            // Gán thông tin hồ sơ
            trainer.setCertification(request.getCertification());
            trainer.setBio(request.getBio());
            trainer.setYoe(request.getYoe());
            trainer.setUpdatedAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));

            userRepository.save(trainer);

            // Gửi email xác nhận
            try {
                String subject = "Chúc mừng! Bạn đã trở thành Huấn luyện viên";
                String htmlContent = String.format("""
                <h2>Xin chào %s,</h2>
                <p>Yêu cầu trở thành Huấn luyện viên của bạn đã được <strong>chấp nhận</strong>.</p>
                <p>Bạn có thể truy cập hệ thống để cập nhật thông tin và bắt đầu công việc.</p>
                <p>
                    <a href="http://localhost:3000/"
                       style="display: inline-block;
                              padding: 10px 20px;
                              font-size: 16px;
                              color: #ffffff;
                              background-color: #007bff;
                              text-decoration: none;
                              border-radius: 4px;">
                        Đăng nhập ngay
                    </a>
                </p>
                <br/>
                <p>Trân trọng,</p>
                <p>Smoking Team</p>
                """, trainer.getFullName());

                emailService.sendHtmlEmail(trainer.getEmail(), subject, htmlContent);
            } catch (Exception e) {
                System.err.println("Không thể gửi email xác nhận: " + e.getMessage());
            }
        }

        return trainerRequestsRepository.save(request);
    }

}
