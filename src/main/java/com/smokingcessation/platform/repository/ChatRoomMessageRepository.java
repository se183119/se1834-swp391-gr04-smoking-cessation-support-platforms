package com.smokingcessation.platform.repository;

import com.smokingcessation.platform.entity.ChatRoomMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomMessageRepository extends JpaRepository<ChatRoomMessage, Long> {
    List<ChatRoomMessage> findByChatRoomId(Long chatRoomId);
}