package com.bookstore.book_sell_service.controller;

import com.bookstore.book_sell_service.dto.request.ApiResponse;
import com.bookstore.book_sell_service.dto.request.TacGiaRequest;
import com.bookstore.book_sell_service.dto.responses.TacGiaResponse;
import com.bookstore.book_sell_service.services.TacGiaService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tac-gia")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TacGiaController {
    TacGiaService tacGiaService;

    @PostMapping
    public ApiResponse<TacGiaResponse> createTacGia(@RequestBody TacGiaRequest request) {
        return ApiResponse.<TacGiaResponse>builder()
                .message("Thêm tác giả thành công")
                .result(tacGiaService.createTacGia(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<TacGiaResponse>> getAllTacGias() {
        return ApiResponse.<List<TacGiaResponse>>builder()
                .result(tacGiaService.getAllTacGias())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<TacGiaResponse> getTacGia(@PathVariable Long id) {
        return ApiResponse.<TacGiaResponse>builder()
                .result(tacGiaService.getTacGia(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<TacGiaResponse> updateTacGia(@PathVariable Long id, @RequestBody TacGiaRequest request) {
        return ApiResponse.<TacGiaResponse>builder()
                .message("Cập nhật tác giả thành công")
                .result(tacGiaService.updateTacGia(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTacGia(@PathVariable Long id) {
        tacGiaService.deleteTacGia(id);
        return ApiResponse.<Void>builder()
                .message("Đã xóa tác giả")
                .build();
    }
}