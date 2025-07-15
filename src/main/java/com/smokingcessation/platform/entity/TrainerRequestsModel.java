package com.smokingcessation.platform.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.smokingcessation.platform.enums.TrainerRequestStatus;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "trainer_requests")
public class TrainerRequestsModel extends BaseModel {

    @Lob
    private String certification;
    private TrainerRequestStatus status = TrainerRequestStatus.PENDING;
    private String bio;
    private Float yoe;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
