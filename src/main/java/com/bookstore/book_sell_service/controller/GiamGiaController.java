package com.bookstore.book_sell_service.controller;

import com.bookstore.book_sell_service.dto.request.ApiResponse;
import com.bookstore.book_sell_service.dto.request.GiamGiaRequest;
import com.bookstore.book_sell_service.dto.responses.GiamGiaResponse;
import com.bookstore.book_sell_service.services.GiamGiaService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/giam-gia")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GiamGiaController {
    GiamGiaService giamGiaService;

    @PostMapping
    public ApiResponse<GiamGiaResponse> createGiamGia(@RequestBody GiamGiaRequest request) {
        return ApiResponse.<GiamGiaResponse>builder()
                .message("Tạo mã giảm giá thành công")
                .result(giamGiaService.createGiamGia(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<GiamGiaResponse>> getAllGiamGias() {
        return ApiResponse.<List<GiamGiaResponse>>builder()
                .result(giamGiaService.getAllGiamGias())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<GiamGiaResponse> getGiamGia(@PathVariable Long id) {
        return ApiResponse.<GiamGiaResponse>builder()
                .result(giamGiaService.getGiamGia(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<GiamGiaResponse> updateGiamGia(@PathVariable Long id, @RequestBody GiamGiaRequest request) {
        return ApiResponse.<GiamGiaResponse>builder()
                .message("Cập nhật giảm giá thành công")
                .result(giamGiaService.updateGiamGia(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteGiamGia(@PathVariable Long id) {
        giamGiaService.deleteGiamGia(id);
        return ApiResponse.<Void>builder()
                .message("Đã xóa mã giảm giá")
                .build();
    }
}