package com.bookstore.book_sell_service.controller;


import com.bookstore.book_sell_service.dto.request.ApiResponse;
import com.bookstore.book_sell_service.dto.request.GioHang.GioHangDelete;
import com.bookstore.book_sell_service.dto.request.GioHang.GioHangRequest;
import com.bookstore.book_sell_service.dto.request.GioHang.GioHangUpdate;
import com.bookstore.book_sell_service.dto.responses.GioHangResponse;
import com.bookstore.book_sell_service.services.GioHangService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequestMapping("/gh")
public class GioHangController {

    GioHangService gioHangService;

    @PostMapping("/addGH")
    public ApiResponse<Void> gioHangApiResponse (@RequestBody GioHangRequest request){
        gioHangService.addSachGioHang(request);
        return  ApiResponse.<Void>builder()
                .message("oko")
                .build();
    }
    @PutMapping ("/update-soluong")
    public ApiResponse<Void> gioHangUpdate (@RequestBody GioHangUpdate update){
        gioHangService.updateSoLuong(update);
        return ApiResponse.<Void>builder()
                .message("oko")
                .build();
    }
    @DeleteMapping("/delete-GH/{maSach}")
    public ApiResponse<Void> gioHangDelete(@PathVariable Long maSach) {
        gioHangService.deleteSach(maSach);
        return ApiResponse.<Void>builder()
                .message("oko")
                .build();
    }
    @GetMapping("/GH_of_user")
    public ApiResponse<GioHangResponse> gioHangOfUser (){
        GioHangResponse gioHangResponse = gioHangService.getGHOfMaKH();
        return ApiResponse.<GioHangResponse>builder()
                .message("oko")
                .result(gioHangResponse)
                .build();
    }


}
