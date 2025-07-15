package com.smokingcessation.platform.repository;

import com.smokingcessation.platform.entity.TrainerRequestsModel;
import com.smokingcessation.platform.enums.TrainerRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ITrainerRequestsRepository  extends JpaRepository<TrainerRequestsModel, Long>, JpaSpecificationExecutor<TrainerRequestsModel> {
    TrainerRequestsModel findByUserIdAndStatus(Long userId, TrainerRequestStatus status);
    List<TrainerRequestsModel> findAllByUserId(Long userId);
    List<TrainerRequestsModel> findAllByStatus(TrainerRequestStatus status);

}