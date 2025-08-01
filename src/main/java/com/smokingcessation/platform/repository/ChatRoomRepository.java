package com.smokingcessation.platform.repository;

import com.smokingcessation.platform.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findByCreatedById(long createdById);
    List<ChatRoom> findByParticipantId(long participantId);
    Optional<ChatRoom> findByCreatedByIdAndParticipantIdOrCreatedByIdAndParticipantId(
            long c1, long p1, long c2, long p2);


    @Query("SELECT COUNT(DISTINCT c.createdById) FROM ChatRoom c WHERE c.participantId = :userId")
    int countDistinctUsersSupported(@Param("userId") long userId);


}

