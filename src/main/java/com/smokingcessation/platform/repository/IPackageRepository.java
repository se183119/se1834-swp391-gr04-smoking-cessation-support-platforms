package com.smokingcessation.platform.repository;

import com.smokingcessation.platform.entity.PackageModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IPackageRepository  extends JpaRepository<PackageModel, Long>, JpaSpecificationExecutor<PackageModel> {
    Page<PackageModel> findByNameContaining(String name, Pageable pageable);
    Page<PackageModel> findByAuthorId(Long userId, Pageable pageable);
    @Query("SELECT p FROM PackageModel p WHERE p.isDefault = true")
    PackageModel findDefaultPackage();
}
