package com.smokingcessation.platform.repository;

import com.smokingcessation.platform.entity.MembershipPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MembershipPackageRepository extends JpaRepository<MembershipPackage, Long> {

    List<MembershipPackage> findByStatus(MembershipPackage.PackageStatus status);

    List<MembershipPackage> findByStatusOrderByPriceAsc(MembershipPackage.PackageStatus status);
}
