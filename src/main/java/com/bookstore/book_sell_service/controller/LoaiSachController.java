package com.bookstore.book_sell_service.controller;

import com.bookstore.book_sell_service.dto.request.ApiResponse;
import com.bookstore.book_sell_service.dto.request.LoaiSach.LoaiSachRequest;
import com.bookstore.book_sell_service.entity.LoaiSach;
import com.bookstore.book_sell_service.services.LoaiSachService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/loai-sach")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LoaiSachController {
    LoaiSachService loaiSachService;

    @PostMapping
    public ApiResponse<LoaiSach> create(@RequestBody LoaiSachRequest request) {
        return ApiResponse.<LoaiSach>builder()
                .result(loaiSachService.createLoaiSach(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<LoaiSach>> getAll() {
        return ApiResponse.<List<LoaiSach>>builder()
                .result(loaiSachService.getAllLoaiSach())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<LoaiSach> getById(@PathVariable Long id) {
        return ApiResponse.<LoaiSach>builder()
                .result(loaiSachService.getLoaiSachById(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<LoaiSach> update(@PathVariable Long id, @RequestBody LoaiSachRequest request) {
        return ApiResponse.<LoaiSach>builder()
                .result(loaiSachService.updateLoaiSach(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        loaiSachService.deleteLoaiSach(id);
        return ApiResponse.<String>builder()
                .message("Đã xóa loại sách thành công")
                .build();
    }
}