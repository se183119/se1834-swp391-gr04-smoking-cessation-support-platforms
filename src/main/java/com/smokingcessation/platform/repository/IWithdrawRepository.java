package com.smokingcessation.platform.repository;

import com.smokingcessation.platform.entity.WithdrawModel;
import com.smokingcessation.platform.enums.WithdrawStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IWithdrawRepository extends JpaRepository<WithdrawModel, Long> {
    List<WithdrawModel> findByUserId(Long userId);
    Page<WithdrawModel> findByStatus(WithdrawStatus status, Pageable pageable);
    @Query
            ("SELECT COALESCE(SUM(w.amount), 0) FROM WithdrawModel w WHERE w.user.id = :userId AND w.status = 'APPROVED'")
    double sumApprovedWithdrawAmountByUser(@Param("userId") Long userId);


}
