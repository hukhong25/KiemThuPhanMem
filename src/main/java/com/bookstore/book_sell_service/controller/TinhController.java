package com.bookstore.book_sell_service.controller;

import com.bookstore.book_sell_service.dto.request.ApiResponse;
import com.bookstore.book_sell_service.dto.request.TinhRequest;
import com.bookstore.book_sell_service.entity.Tinh;
import com.bookstore.book_sell_service.services.TinhService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tinh")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TinhController {
    TinhService tinhService;

    @PostMapping
    public ApiResponse<Tinh> createTinh(@RequestBody TinhRequest request) {
        ApiResponse<Tinh> apiResponse = new ApiResponse<>();
        apiResponse.setResult(tinhService.createTinh(request));
        return apiResponse;
    }
    @GetMapping
    ApiResponse<List<Tinh>> getTinhs(){
        return ApiResponse.<List<Tinh>>builder()
                .result(tinhService.getTinhs())
                .build();
    }
}
