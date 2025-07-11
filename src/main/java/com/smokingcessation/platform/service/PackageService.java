package com.smokingcessation.platform.service;


import com.smokingcessation.platform.dto.PackageCreateUpdateDTO;
import com.smokingcessation.platform.entity.PackageModel;
import com.smokingcessation.platform.repository.IPackageRepository;
import com.smokingcessation.platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PackageService {

    @Autowired
    private IPackageRepository packageRepository;

    @Autowired
    private UserRepository userRepository;

    public PackageModel createUpdatePackage(PackageCreateUpdateDTO packageModel) {
        var existingPackage = packageRepository.findById(packageModel.getId());

        if (existingPackage.isPresent()) {

            var user = userRepository.findById(packageModel.getUserId());

            if (user.isEmpty()) {
                throw new IllegalArgumentException("User not found with ID: " + packageModel.getUserId());
            }

            if(packageModel.getUserId() != (existingPackage.get().getAuthor().getId())) {
                throw new IllegalArgumentException("Cannot change the user of an existing package.");
            }


            PackageModel updatedPackage = existingPackage.get();
            updatedPackage.setName(packageModel.getName());
            updatedPackage.setDescription(packageModel.getDescription());
            updatedPackage.setTotalDays(packageModel.getTotalDays());
            updatedPackage.setImageUrl(packageModel.getImageUrl());
            updatedPackage.setPrice(packageModel.getPrice());
            updatedPackage.setFeatured(packageModel.getFeatured());
            updatedPackage.setPackageType(packageModel.getPackageType());
            updatedPackage.setSalePrice(packageModel.getSalePrice());
            updatedPackage.setActive(packageModel.isActive());
            updatedPackage.setPrice(packageModel.getPrice());
            updatedPackage.setSalePrice(packageModel.getSalePrice());
            return packageRepository.save(updatedPackage);
        } else {

            var user = userRepository.findById(packageModel.getUserId());

            if (user.isEmpty()) {
                throw new IllegalArgumentException("User not found with ID: " + packageModel.getUserId());
            }

            PackageModel newPackage = new PackageModel();
            newPackage.setName(packageModel.getName());
            newPackage.setDescription(packageModel.getDescription());
            newPackage.setTotalDays(packageModel.getTotalDays());
            newPackage.setImageUrl(packageModel.getImageUrl());
            newPackage.setFeatured(packageModel.getFeatured());
            newPackage.setPackageType(packageModel.getPackageType());
            newPackage.setPrice(packageModel.getPrice());
            newPackage.setSalePrice(packageModel.getSalePrice());
            newPackage.setActive(true);
            newPackage.setAuthor(user.get());
            packageRepository.save(newPackage);


            return newPackage;
        }

    }


    public PackageModel getPackageById(Long id) {
        return packageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Package not found with ID: " + id));
    }

    public List<PackageModel> getAllPackages() {
        return packageRepository.findAll();
    }


}
