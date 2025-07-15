package com.smokingcessation.platform.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_room_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomMessage extends BaseModel {

    private String content; // Nội dung tin nhắn
    private boolean isUser = true;

    @ManyToOne
    @JoinColumn(name = "chatroom_id", nullable = false)
    @JsonBackReference
    private ChatRoom chatRoom;
}
