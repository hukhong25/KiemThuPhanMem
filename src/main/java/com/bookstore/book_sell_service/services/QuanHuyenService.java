package com.bookstore.book_sell_service.services;

import com.bookstore.book_sell_service.dto.request.QuanHuyenRequest;
import com.bookstore.book_sell_service.entity.QuanHuyen;
import com.bookstore.book_sell_service.entity.Tinh;
import com.bookstore.book_sell_service.mapper.QuanHuyenMapper;
import com.bookstore.book_sell_service.repositories.QuanHuyenRepository;
import com.bookstore.book_sell_service.repositories.TinhRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class QuanHuyenService {
    QuanHuyenRepository quanHuyenRepository;
    TinhRepository tinhRepository;
    QuanHuyenMapper quanHuyenMapper;
    public QuanHuyen createQuanHuyen(QuanHuyenRequest request){
        Tinh tinh = tinhRepository.findById(request.getMaTinh())
                .orElseThrow(() -> new RuntimeException("Tinh not found"));
        QuanHuyen quanHuyen = quanHuyenMapper.toQuanHuyen(request);
        quanHuyen.setTinh(tinh);
        return quanHuyenRepository.save(quanHuyen);
    }
    public List<QuanHuyen> getQuanHuyens(){
        return quanHuyenRepository.findAll();
    }

}
