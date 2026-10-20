package com.bookstore.book_sell_service.controller;

import com.bookstore.book_sell_service.dto.request.ApiResponse;
import com.bookstore.book_sell_service.dto.request.NhanVien.NhanVienCreationalRequest;
import com.bookstore.book_sell_service.dto.request.NhanVien.NhanVienUpdateRequest;
import com.bookstore.book_sell_service.dto.responses.NVResponse;
import com.bookstore.book_sell_service.services.NhanVienService;
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
@RequestMapping("/nv")
public class NhanVienController {

    NhanVienService nhanVienService;

    @PostMapping
    ApiResponse<NVResponse> createNhanVien(@RequestBody NhanVienCreationalRequest request){
        ApiResponse<NVResponse> apiResponse = new ApiResponse<>();
           apiResponse.setResult(nhanVienService.createNhanVien(request));
        return apiResponse;
    }

    @GetMapping
    ApiResponse<List<NVResponse>> getAllNhanViens() {
        logAuthenticationDetails();
        return ApiResponse.<List<NVResponse>>builder()
                .result(nhanVienService.getAllNhanViens())
                .build();
    }

    private void logAuthenticationDetails() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("User: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority ->
                log.info(grantedAuthority.getAuthority()));
    }


    @GetMapping("/{maNV}")
    ApiResponse<NVResponse> getNhanVien(@PathVariable Long maNV){
        return ApiResponse.<NVResponse>builder()
                .result(nhanVienService.getNhanVien(maNV))
                .build();
    }

    @PutMapping("/{maNV}")
    ApiResponse<NVResponse> updateNhanVien(@PathVariable Long maNV,@RequestBody NhanVienUpdateRequest request){
       return ApiResponse.<NVResponse>builder()
                .result(nhanVienService.updateNhanVien(maNV,request))
                .build();
    }

    @DeleteMapping("/{maNV}")
    String deleteNhanVien(@PathVariable Long maNV){
        nhanVienService.deleteNhanVien(maNV);
        return "NV has been deleted";
    }

    @GetMapping("/myInfo")
    ApiResponse<NVResponse> getMyInfo(){
        return ApiResponse.<NVResponse>builder()
                .result(nhanVienService.getMyInfo())
                .build();
    }
}
