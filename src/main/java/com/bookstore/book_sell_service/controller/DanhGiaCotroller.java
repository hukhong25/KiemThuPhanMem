package com.bookstore.book_sell_service.controller;

import com.bookstore.book_sell_service.dto.request.ApiResponse;
import com.bookstore.book_sell_service.dto.request.DanhGia.DanhGiaRequest;
import com.bookstore.book_sell_service.dto.responses.DanhGiaResponse;
import com.bookstore.book_sell_service.entity.DanhGia;
import com.bookstore.book_sell_service.services.DanhGiaService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/danh_gia")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class DanhGiaCotroller {

    DanhGiaService danhGiaService;

    @PostMapping
    public ApiResponse<DanhGia> creataDanhGia (@RequestBody DanhGiaRequest danhGiaRequest){
        DanhGia danhGia =  danhGiaService.createDanhGia(danhGiaRequest);
        return ApiResponse.<DanhGia>builder()
                .message("Thanh cong")
                .result(danhGia)
                .build();
    }

    @GetMapping
    public ApiResponse<List<DanhGiaResponse>> getAll(){
        return ApiResponse.<List<DanhGiaResponse>>builder()
                .result(danhGiaService.getAllDanhGia())
                .build();
    }

    @GetMapping("/{maSach}")
    public ApiResponse<List<DanhGiaResponse>> getAllByMaSach(@PathVariable Long maSach){
        return ApiResponse.<List<DanhGiaResponse>>builder()
                .result(danhGiaService.getAllByMaSach(maSach))
                .build();
    }
    @DeleteMapping("/{maDanhGia}")
    public ApiResponse<Void> deleteDG(@PathVariable Long maDanhGia)
    {
        danhGiaService.deleteBL(maDanhGia);
        return ApiResponse.<Void>builder()
                .message("oko")
                .build();
    }
}
