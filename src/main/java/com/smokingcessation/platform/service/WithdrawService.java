package com.smokingcessation.platform.service;

import com.smokingcessation.platform.config.EmailService;
import com.smokingcessation.platform.dto.WithdrawRequestDTO;
import com.smokingcessation.platform.entity.ChatRoomMessage;
import com.smokingcessation.platform.entity.PackageModel;
import com.smokingcessation.platform.entity.User;
import com.smokingcessation.platform.entity.WithdrawModel;
import com.smokingcessation.platform.enums.WithdrawStatus;
import com.smokingcessation.platform.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
public class WithdrawService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IWithdrawRepository withdrawRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ChatRoomMessageRepository messageRepo;

    @Autowired
    private ChatRoomRepository chatRoomRepo;

    @Autowired
    private IPackageRepository packageRepo;

    // Tính balance động theo số lượng tin nhắn
    private long calculateUserBalance(User user) {
        int totalUnclaimedMessages = messageRepo.countUnclaimedMessagesByParticipantId(user.getId());

        PackageModel defaultPackage = packageRepo.findDefaultPackage();
        if (defaultPackage == null || defaultPackage.getLimitMessages() == 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Gói mặc định không hợp lệ");
        }

        double rawCostPerMessage = (double) defaultPackage.getSalePrice() / defaultPackage.getLimitMessages();
        long costPerMessage = Math.round(rawCostPerMessage * 0.7);

        return totalUnclaimedMessages * costPerMessage;
    }

    @Transactional
    public WithdrawModel requestWithdraw(Long userId, WithdrawRequestDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));

        long balance = calculateUserBalance(user);
        double totalWithdrawn = withdrawRepository.sumApprovedWithdrawAmountByUser(userId);
        long availableBalance = balance - (long) totalWithdrawn;

//        if (dto.getAmount() > availableBalance) {
//            throw new IllegalArgumentException("Số dư không đủ để rút");
//        }

        WithdrawModel withdraw = new WithdrawModel();
        withdraw.setUser(user);
        withdraw.setAmount(dto.getAmount());
        withdraw.setBankName(dto.getBankName());
        withdraw.setBankAccount(dto.getBankAccount());
        withdraw.setBankAccountName(dto.getBankAccountName());
        withdraw.setStatus(WithdrawStatus.PENDING);

        withdraw = withdrawRepository.save(withdraw);
        sendWithdrawRequestEmail(user, withdraw);
        List<ChatRoomMessage> unclaimedMessages = messageRepo.findUnclaimedMessagesByParticipantId(user.getId());
        for (ChatRoomMessage msg : unclaimedMessages) {
            msg.setClaim(true);
        }
        messageRepo.saveAll(unclaimedMessages);
        return withdraw;
    }

    public List<WithdrawModel> getWithdrawsByUser(Long userId) {
        return withdrawRepository.findByUserId(userId);
    }

    public List<WithdrawModel> getByPaging() {
        return  withdrawRepository.findAll();
    }

    @Transactional
    public WithdrawModel updateStatus(Long withdrawId, String newStatus, String imageUrl, String note) {
        WithdrawModel withdraw = withdrawRepository.findById(withdrawId)
                .orElseThrow(() -> new IllegalArgumentException("Yêu cầu rút tiền không tồn tại"));

        if (withdraw.getStatus() != WithdrawStatus.PENDING) {
            throw new IllegalStateException("Chỉ có thể cập nhật yêu cầu đang chờ xử lý");
        }

        WithdrawStatus status = WithdrawStatus.valueOf(newStatus.toUpperCase());
        withdraw.setStatus(status);
        withdraw.setAdminNote(note);
        withdraw.setImageUrl(imageUrl);

        withdraw = withdrawRepository.save(withdraw);
        sendStatusUpdateEmail(withdraw.getUser(), withdraw);
        return withdraw;
    }

    private void sendWithdrawRequestEmail(User user, WithdrawModel withdraw) {
        String subject = "📤 Yêu cầu rút tiền đã được ghi nhận";
        String html = String.format("""
            <p>Chào %s,</p>
            <p>Yêu cầu rút tiền <strong>%,.0f VNĐ</strong> của bạn đã được ghi nhận.</p>
            <ul>
              <li>Ngân hàng: <strong>%s</strong></li>
              <li>Số tài khoản: <strong>%s</strong></li>
              <li>Tên chủ tài khoản: <strong>%s</strong></li>
            </ul>
            <p>Trạng thái hiện tại: <strong>%s</strong></p>
            """,
                user.getFullName(),
                withdraw.getAmount(),
                withdraw.getBankName(),
                withdraw.getBankAccount(),
                withdraw.getBankAccountName(),
                withdraw.getStatus().name()
        );

        try {
            emailService.sendHtmlEmail(user.getEmail(), subject, wrapEmailBody(html));
        } catch (Exception e) {
            System.err.println("Gửi email thất bại: " + e.getMessage());
        }
    }

    private void sendStatusUpdateEmail(User user, WithdrawModel withdraw) {
        String subject = "🔔 Cập nhật trạng thái yêu cầu rút tiền";
        String html = String.format("""
            <p>Chào %s,</p>
            <p>Yêu cầu rút tiền của bạn đã được cập nhật:</p>
            <ul>
              <li>Số tiền: <strong>%,.0f VNĐ</strong></li>
              <li>Trạng thái mới: <strong>%s</strong></li>
              <li>Ghi chú từ admin: <em>%s</em></li>
            </ul>
            """,
                user.getFullName(),
                withdraw.getAmount(),
                withdraw.getStatus().name(),
                withdraw.getAdminNote() != null ? withdraw.getAdminNote() : "(Không có)"
        );

        try {
            emailService.sendHtmlEmail(user.getEmail(), subject, wrapEmailBody(html));
        } catch (Exception e) {
            System.err.println("Gửi email cập nhật thất bại: " + e.getMessage());
        }
    }

    private String wrapEmailBody(String content) {
        return """
            <!DOCTYPE html>
            <html lang="vi">
            <head><meta charset="UTF-8"></head>
            <body style="font-family:Arial,sans-serif;line-height:1.6;color:#333;">
            """ + content + """
            <p style="margin-top:30px;">Trân trọng,<br><strong>SCSP Team</strong></p>
            </body>
            </html>
            """;
    }

    @Transactional
    public void deleteWithdraw(Long userId, Long withdrawId) {
        WithdrawModel withdraw = withdrawRepository.findById(withdrawId)
                .orElseThrow(() -> new IllegalArgumentException("Yêu cầu không tồn tại"));

        if (!withdraw.getUser().getId().equals(userId)) {
            throw new SecurityException("Bạn không có quyền xoá yêu cầu này");
        }

        if (withdraw.getStatus() != WithdrawStatus.PENDING) {
            throw new IllegalStateException("Chỉ có thể xoá yêu cầu đang chờ xử lý");
        }

        withdraw.setStatus(WithdrawStatus.CANCELLED);
        withdrawRepository.save(withdraw);
    }
}
