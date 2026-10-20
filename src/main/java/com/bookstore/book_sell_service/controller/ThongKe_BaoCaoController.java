package com.bookstore.book_sell_service.controller;

import com.bookstore.book_sell_service.dto.request.ApiResponse;
import com.bookstore.book_sell_service.dto.responses.ThongKeResponse;
import com.bookstore.book_sell_service.services.DoanhThu_ThongKe_BaoCaoAService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/baocao_doanhthu")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class ThongKe_BaoCaoController {

    DoanhThu_ThongKe_BaoCaoAService doanhThuThongKeBaoCao;

    @GetMapping
    public ApiResponse<ThongKeResponse> thongKeResponseApiResponse(){
        return ApiResponse.<ThongKeResponse>builder()
                .result(doanhThuThongKeBaoCao.thongKeResponse())
                .build();
    }
}
