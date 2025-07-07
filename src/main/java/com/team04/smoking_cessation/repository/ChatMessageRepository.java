package com.team04.smoking_cessation.repository;

import com.team04.smoking_cessation.entity.ChatMessage;
import com.team04.smoking_cessation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT cm FROM ChatMessage cm WHERE " +
           "(cm.sender = :user1 AND cm.receiver = :user2) OR " +
           "(cm.sender = :user2 AND cm.receiver = :user1) " +
           "ORDER BY cm.createdAt ASC")
    List<ChatMessage> findChatHistory(@Param("user1") User user1, @Param("user2") User user2);

    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.receiver = :coach AND cm.isRead = false")
    Long countUnreadMessagesForCoach(@Param("coach") User coach);

    List<ChatMessage> findByReceiverAndIsReadFalse(User receiver);

    List<ChatMessage> findBySenderAndReceiverOrderByCreatedAtDesc(User sender, User receiver);
}
