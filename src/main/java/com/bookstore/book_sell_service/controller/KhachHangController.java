package com.bookstore.book_sell_service.controller;

import com.bookstore.book_sell_service.dto.request.ApiResponse;
import com.bookstore.book_sell_service.dto.request.KhachHang.KhachHangCreationRequest;
import com.bookstore.book_sell_service.dto.request.KhachHang.KhachHangUpdatePassWordRequest;
import com.bookstore.book_sell_service.dto.request.KhachHang.KhachHangUpdateRequest;
import com.bookstore.book_sell_service.dto.responses.KHResponse;
import com.bookstore.book_sell_service.entity.KhachHang;
import com.bookstore.book_sell_service.services.KhachHangService;
import jakarta.validation.Valid;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequestMapping("/kh")
public class KhachHangController {

    KhachHangService khachHangService;

    @PostMapping
    public ApiResponse<KhachHang> createKhachHang(@RequestBody @Valid KhachHangCreationRequest request){
        ApiResponse<KhachHang> apiResponse = new ApiResponse<>();
        apiResponse.setResult(khachHangService.createKhachHang(request));
        return apiResponse;
    }

    @GetMapping
    public ApiResponse<List<KHResponse>> getAllKhachHangs(){
        var authentication= SecurityContextHolder.getContext().getAuthentication();
        log.info("User: {}",authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        return ApiResponse.<List<KHResponse>>builder()
                .result(khachHangService.getAllKhachHangs())
                .build();
    }

    @GetMapping("/{maKH}")
    public ApiResponse<KhachHang> getKhachHang(@PathVariable String maKH){
        return ApiResponse.<KhachHang>builder()
                .result(khachHangService.getKhachHang(maKH))
                .build();
    }

    @PutMapping("/{maKH}")
    public ApiResponse<KHResponse> updateUser(@PathVariable String maKH
            , @RequestBody @Valid KhachHangUpdateRequest request ){
        return ApiResponse.<KHResponse>builder()
                .result(khachHangService.updateKH(maKH,request))
                .build();
    }
    @PutMapping("/password/{maKH}")
    public ApiResponse<Void> updatePassWord(@PathVariable String maKH
            , @RequestBody @Valid KhachHangUpdatePassWordRequest request ){
        khachHangService.updatePassWord(maKH,request);
        return ApiResponse.<Void>builder()
                .message("Da cap nhat thanh cong")
                .build();
    }
    @DeleteMapping("/{maKH}")
    public String deleteUser(@PathVariable String maKH){
        khachHangService.deleteKH(maKH);
        return "KH has been deleted";
    }

    @GetMapping("/myInfo")
    ApiResponse<KHResponse> getMyInfo(){
        return ApiResponse.<KHResponse>builder()
                .code(100)
                .result(khachHangService.getMyInfo())
                .build();
    }
}
