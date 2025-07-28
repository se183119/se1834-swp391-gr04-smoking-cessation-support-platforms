package com.smokingcessation.platform.controller;

import com.smokingcessation.platform.dto.*;
import com.smokingcessation.platform.entity.WithdrawModel;
import com.smokingcessation.platform.service.WithdrawService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/withdraws")
@CrossOrigin(origins = "*")
public class WithdrawController {

    @Autowired
    private WithdrawService withdrawService;

    @Autowired
    private ModelMapper mapper;

    /**
     * Gửi yêu cầu rút tiền
     */
    @PostMapping("/user/{userId}")
    public WithdrawDTO requestWithdraw(
            @PathVariable Long userId,
            @RequestBody WithdrawRequestDTO dto) {

        WithdrawModel withdraw = withdrawService.requestWithdraw(userId, dto);
        return toDTO(withdraw);
    }

    /**
     * Lấy danh sách yêu cầu rút tiền của user cụ thể
     */
    @GetMapping("/user/{userId}")
    public List<WithdrawDTO> getMyWithdraws(@PathVariable Long userId) {
        List<WithdrawModel> list = withdrawService.getWithdrawsByUser(userId);
        return list.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<WithdrawDTO> getAllWithdraws() {
        List<WithdrawModel> list = withdrawService.getByPaging();
        return list.stream().map(this::toDTO).collect(Collectors.toList());
    }

//    /**
//     * Lấy danh sách rút tiền theo phân trang (dành cho admin)
//     */
//    @GetMapping("/paging")
//    public Page<WithdrawDTO> getWithdrawsByPaging(
//            PageableRequestDTO request,
//            @RequestParam(required = false) String status) {
//
//        request.validate();
//        Page<WithdrawModel> page = withdrawService.getByPaging(request.toPageable(), status);
//        return page.map(this::toDTO);
//    }

    /**
     * Admin cập nhật trạng thái yêu cầu
     */
    @PutMapping("/update-status")
    public WithdrawDTO updateWithdrawStatus(@RequestBody WithdrawUpdateStatusDTO dto) {
        WithdrawModel updated = withdrawService.updateStatus(
                dto.getId(),
                dto.getStatus(),
                dto.getImageUrl(),
                dto.getNote()
        );
        return toDTO(updated);
    }

//    /**
//     * User cập nhật thông tin yêu cầu rút tiền
//     */
//    @PutMapping("/update/user/{userId}")
//    public WithdrawDTO updateWithdraw(
//            @PathVariable Long userId,
//            @RequestBody WithdrawRequestDTO dto) {
//
//        WithdrawModel updated = withdrawService.updateWithdraw(userId, dto);
//        return toDTO(updated);
//    }

    /**
     * User xoá yêu cầu rút tiền
     */
    @DeleteMapping("/user/{userId}/withdraw/{id}")
    public String deleteWithdraw(
            @PathVariable Long userId,
            @PathVariable Long id) {

        withdrawService.deleteWithdraw(userId, id);
        return "Xoá yêu cầu thành công";
    }

    private WithdrawDTO toDTO(WithdrawModel model) {
        return mapper.map(model, WithdrawDTO.class);
    }
}
