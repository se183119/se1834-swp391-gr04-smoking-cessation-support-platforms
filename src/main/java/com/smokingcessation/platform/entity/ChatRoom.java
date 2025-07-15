package com.smokingcessation.platform.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat_rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom extends BaseModel {
    private String name; // Tên phòng chat
    private String description; // Mô tả phòng chat
    private long createdById;
    private long participantId;

    @OneToMany
    (mappedBy = "chatRoom", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoomMessage> messages = new ArrayList<>(); // Danh sách tin nhắn trong phòng chat
}
