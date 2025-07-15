package com.smokingcessation.platform.controller;


import com.smokingcessation.platform.dto.PackageCreateUpdateDTO;
import com.smokingcessation.platform.dto.PackageResponseDTO;
import com.smokingcessation.platform.entity.PackageModel;
import com.smokingcessation.platform.service.PackageService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/packages")
@CrossOrigin(origins = "*")
public class PackageController {

    @Autowired
    PackageService packageService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/create-update-package")
    public ResponseEntity<PackageResponseDTO
            > createUpdatePackage(
            @RequestBody PackageCreateUpdateDTO dto) {


        PackageModel packageModel = packageService.createUpdatePackage(dto);

        PackageResponseDTO packageResponseDTO = modelMapper.map(packageModel, PackageResponseDTO.class);
        return ResponseEntity.ok(packageResponseDTO);
    }
    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<PackageResponseDTO> getById(@PathVariable Long id) {
        PackageModel packageModel = packageService.getPackageById(id);
        PackageResponseDTO packageResponseDTO = modelMapper.map(packageModel, PackageResponseDTO.class);
        return  ResponseEntity.ok(packageResponseDTO);
    }


    @GetMapping("/get-all")
    public ResponseEntity<List<PackageResponseDTO>> getAll() {
        List<PackageModel> packageModels = packageService.getAllPackages();
        List<PackageResponseDTO> packageResponseDTOS = packageModels.stream()
                .map(packageModel -> modelMapper.map(packageModel, PackageResponseDTO.class))
                .toList();
        return ResponseEntity.ok(packageResponseDTOS);
    }

}
