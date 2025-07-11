package com.smokingcessation.platform.repository;

import com.smokingcessation.platform.entity.UserPackageModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUserPackageRepository  extends JpaRepository<UserPackageModel, Long>, JpaSpecificationExecutor<UserPackageModel> {

}

