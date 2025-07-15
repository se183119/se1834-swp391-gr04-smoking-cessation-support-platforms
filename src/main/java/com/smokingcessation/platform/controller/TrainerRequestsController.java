package com.smokingcessation.platform.controller;


import com.smokingcessation.platform.dto.TrainerRequestsDTO;
import com.smokingcessation.platform.dto.TrainerResponseDTO;
import com.smokingcessation.platform.entity.TrainerRequestsModel;
import com.smokingcessation.platform.enums.TrainerRequestStatus;
import com.smokingcessation.platform.repository.ITrainerRequestsRepository;
import com.smokingcessation.platform.service.TrainerRequestsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trainer-requests")
@CrossOrigin(origins = "*")
public class TrainerRequestsController {

    @Autowired
    TrainerRequestsService trainerRequestsService;


    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/create-trainer-request")
    public ResponseEntity<TrainerResponseDTO> createTrainerRequest(
            @RequestBody TrainerRequestsDTO dto) {

        TrainerRequestsModel trainerRequestsModel = trainerRequestsService.createTrainerRequest(dto);
        TrainerResponseDTO trainerResponseDTO = modelMapper.map(trainerRequestsModel, TrainerResponseDTO.class);
        return ResponseEntity.ok(trainerResponseDTO);
    }

    @GetMapping("/get-by-paging")
    public ResponseEntity<List<TrainerResponseDTO>> getByPaging(
            @RequestParam TrainerRequestStatus status) {

        List<TrainerRequestsModel> trainerRequest;
        trainerRequest = trainerRequestsService.findAllByStatus(status);

        List<TrainerResponseDTO> dtoPage = trainerRequest.stream()
                .map(trainerRequestModel -> modelMapper.map(trainerRequestModel, TrainerResponseDTO.class))
                .toList();

        return ResponseEntity.ok(dtoPage);
    }
    @GetMapping("/get-by-user/{id}")
    public ResponseEntity<List<TrainerResponseDTO>> getByUserId(
            @PathVariable Long id
    ) {
        List<TrainerRequestsModel> trainerRequestsModels = trainerRequestsService.findAllByUserId(id);

        List<TrainerResponseDTO> trainerResponseDTOs = trainerRequestsModels.stream()
                .map(trainerRequest -> modelMapper.map(trainerRequest, TrainerResponseDTO.class))
                .toList();

//        return success(trainerResponseDTOs);
        return ResponseEntity.ok(trainerResponseDTOs);
    }


    @GetMapping("/accept-trainer-request/{id}/{status}")
    public ResponseEntity<TrainerResponseDTO> acceptTrainerRequest(@PathVariable Long id, @PathVariable TrainerRequestStatus status) {
        TrainerRequestsModel trainerRequestsModel = trainerRequestsService.changeStatusTrainerRequest(id, status);
        TrainerResponseDTO trainerResponseDTO = modelMapper.map(trainerRequestsModel, TrainerResponseDTO.class);
        return ResponseEntity.ok(trainerResponseDTO);
    }
}
