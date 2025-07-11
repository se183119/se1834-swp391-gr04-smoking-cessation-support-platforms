package com.smokingcessation.platform.repository;

import com.smokingcessation.platform.entity.OrderModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IOrderRepository  extends JpaRepository<OrderModel, Long>, JpaSpecificationExecutor<OrderModel> {
    OrderModel findByCode(long code);
    Page<OrderModel> findAllByUserId(Long userId, Pageable pageable);
}
