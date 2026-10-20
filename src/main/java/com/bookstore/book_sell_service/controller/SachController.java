package com.bookstore.book_sell_service.controller;

import com.bookstore.book_sell_service.dto.request.ApiResponse;
import com.bookstore.book_sell_service.dto.request.Sach.SachCreationalRequest;
import com.bookstore.book_sell_service.dto.request.Sach.SachFilterRequest;
import com.bookstore.book_sell_service.dto.request.Sach.SachUpdateRequest;
import com.bookstore.book_sell_service.dto.responses.SachResponse;
import com.bookstore.book_sell_service.entity.Sach;
import com.bookstore.book_sell_service.search.SachDocument;
import com.bookstore.book_sell_service.services.SachSearchService;
import com.bookstore.book_sell_service.services.SachService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequestMapping("/sach")
public class SachController {

    SachService sachService;
    SachSearchService sachSearchService;


    @PostMapping
    public ApiResponse<SachResponse> createSach(@RequestBody SachCreationalRequest request){
        return ApiResponse.<SachResponse>builder()
                .message("them sach thanh cong")
                .result(sachService.createSach(request))
                .build();

    }

    @GetMapping
    public ApiResponse<List<SachResponse>> getAllBooks() {
        return ApiResponse.<List<SachResponse>>builder()
                .message("Lấy danh sách tất cả sách thành công")
                .result(sachService.getAllSachs())
                .build();
    }

    @PostMapping ("/request-sach")
    public ApiResponse<List<SachResponse>> getAllSachsByPrice(@RequestBody SachFilterRequest request){
        List<SachResponse> sachList = sachService.getAllSachsByPrice(request);
        return ApiResponse.<List<SachResponse>>builder()
                .message("Lay thanh cong")
                .result(sachList)
                .build();
    }

    @GetMapping("/suggest")
    public ApiResponse<List<SachDocument>> suggestSach(@RequestParam String term) {
        List<SachDocument> results = sachSearchService.searchAutocomplete(term);
        return ApiResponse.<List<SachDocument>>builder()
                .message("Kết quả gợi ý cho: " + term)
                .result(results)
                .build();
    }

    /**
     * API Tìm kiếm chính (Fuzzy) - Ví dụ: /kh/search?term=toiet
     */
    @GetMapping("/search")
    public ApiResponse<List<SachDocument>> searchSach(@RequestParam String term) {
        List<SachDocument> results = sachSearchService.searchFuzzy(term);
        return ApiResponse.<List<SachDocument>>builder()
                .message("Kết quả tìm kiếm cho: " + term)
                .result(results)
                .build();
    }

    @GetMapping("/{maSach}")
    public ApiResponse<SachResponse> getSach(@PathVariable Long maSach){
        return ApiResponse.<SachResponse>builder()
                .result(sachService.getSach(maSach))
                .build();
    }

    @PutMapping("/{maSach}")
    public ApiResponse<SachResponse> updateSach(@PathVariable Long maSach,@RequestBody SachUpdateRequest request){
        return ApiResponse.<SachResponse>builder()
                .result(sachService.updateSach(request, maSach))
                .build();
    }

    @DeleteMapping("/{maSach}")
    public String deleteSach(@PathVariable Long maSach){
        sachService.deleteSach(maSach);
        return "book has been deleted";
    }

    @GetMapping("/the-loai/{maLoai}")
    public ApiResponse<List<SachResponse>> getSachsByTheLoai(@PathVariable Long maLoai){
        return ApiResponse.<List<SachResponse>>builder()
                .message("Lấy danh sách sách theo thể loại thành công")
                .result(sachService.getSachsByLoai(maLoai))
                .build();
    }
}
