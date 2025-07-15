package com.smokingcessation.platform.controller;

import com.smokingcessation.platform.entity.ChatRoom;
import com.smokingcessation.platform.entity.ChatRoomMessage;
import com.smokingcessation.platform.repository.ChatRoomMessageRepository;
import com.smokingcessation.platform.repository.ChatRoomRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/chatrooms")
@CrossOrigin(origins = "*") // Cho phép truy cập từ mọi nguồn
public class ChatRoomController {

    private final ChatRoomRepository chatRoomRepo;
    private final ChatRoomMessageRepository messageRepo;



    public ChatRoomController(ChatRoomRepository chatRoomRepo,
                              ChatRoomMessageRepository messageRepo) {
        this.chatRoomRepo = chatRoomRepo;
        this.messageRepo = messageRepo;
    }

    // Tạo mới ChatRoom
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ChatRoom createChatRoom(@RequestBody ChatRoom chatRoom) {
        Optional<ChatRoom> existing = chatRoomRepo
                .findByCreatedByIdAndParticipantIdOrCreatedByIdAndParticipantId(
                        chatRoom.getCreatedById(), chatRoom.getParticipantId(),
                        chatRoom.getParticipantId(), chatRoom.getCreatedById()
                );
        if (existing.isPresent()) {
            return existing.get();
        }
        // 3. Ngược lại thì tạo mới
        return chatRoomRepo.save(chatRoom);
    }

    // Lấy danh sách tất cả ChatRoom
    @GetMapping
    public List<ChatRoom> getAllChatRooms() {
        return chatRoomRepo.findAll();
    }

    // Lấy chi tiết ChatRoom theo ID (bao gồm messages do LAZY load hoặc EAGER tuỳ cấu hình)
    @GetMapping("/{id}")
    public ChatRoom getChatRoom(@PathVariable Long id) {
        return chatRoomRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "ChatRoom not found with id " + id));
    }

    // Gửi tin nhắn mới vào ChatRoom
    @PostMapping("/{id}/messages")
    public ChatRoomMessage sendMessage(
            @PathVariable Long id,
            @RequestBody ChatRoomMessage payload) {

        ChatRoom room = chatRoomRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "ChatRoom not found with id " + id));

        // Gắn phòng chat vào message rồi lưu
        payload.setChatRoom(room);
        return messageRepo.save(payload);
    }

    // Lấy danh sách tất cả messages của một ChatRoom
    @GetMapping("/{id}/messages")
    public List<ChatRoomMessage> getMessages(@PathVariable Long id) {
        // Kiểm tra room tồn tại
        if (!chatRoomRepo.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "ChatRoom not found with id " + id);
        }
        return messageRepo.findByChatRoomId(id);
    }

    // (Tuỳ chọn) Xoá một phòng chat cùng toàn bộ tin nhắn
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChatRoom(@PathVariable Long id) {
        if (!chatRoomRepo.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "ChatRoom not found with id " + id);
        }
        chatRoomRepo.deleteById(id);
    }

    @GetMapping("/created-by/{createdById}")
    public List<ChatRoom> getByCreator(@PathVariable long createdById) {
        return chatRoomRepo.findByCreatedById(createdById);
    }

    /**
     * Lấy tất cả ChatRoom mà user nào đó tham gia
     */
    @GetMapping("/participant/{participantId}")
    public List<ChatRoom> getByParticipant(@PathVariable long participantId) {
        return chatRoomRepo.findByParticipantId(participantId);
    }
}