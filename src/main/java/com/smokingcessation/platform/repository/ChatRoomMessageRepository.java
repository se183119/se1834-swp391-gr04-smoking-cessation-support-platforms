package com.smokingcessation.platform.repository;

import com.smokingcessation.platform.entity.ChatRoomMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomMessageRepository extends JpaRepository<ChatRoomMessage, Long> {
    List<ChatRoomMessage> findByChatRoomId(Long chatRoomId);
    @Query("SELECT COUNT(m) FROM ChatRoomMessage m WHERE m.chatRoom.participantId = :userId AND m.isUser = true")
    int countMessagesByParticipantId(@Param("userId") long userId);

    @Query("SELECT COUNT(DISTINCT c.createdById) FROM ChatRoom c WHERE c.participantId = :userId")
    int countDistinctUsersSupported(@Param("userId") long userId);

    @Query("SELECT COUNT(m) FROM ChatRoomMessage m " +
            "WHERE m.chatRoom.participantId = :userId " +
            "AND m.isUser = true AND m.isClaim = false")
    int countUnclaimedMessagesByParticipantId(@Param("userId") Long userId);

    @Query("SELECT m FROM ChatRoomMessage m " +
            "WHERE m.chatRoom.participantId = :userId " +
            "AND m.isUser = true AND m.isClaim = false")
    List<ChatRoomMessage> findUnclaimedMessagesByParticipantId(@Param("userId") Long userId);

}