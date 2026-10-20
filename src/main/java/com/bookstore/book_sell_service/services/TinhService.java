package com.bookstore.book_sell_service.services;

import com.bookstore.book_sell_service.dto.request.TinhRequest;
import com.bookstore.book_sell_service.entity.Tinh;
import com.bookstore.book_sell_service.mapper.TinhMapper;
import com.bookstore.book_sell_service.repositories.TinhRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TinhService {
    TinhRepository tinhRepository;
    TinhMapper tinhMapper;

    public Tinh createTinh(TinhRequest request) {
        Tinh tinh = tinhMapper.toTinh(request);
        return tinhRepository.save(tinh);
    }

    public List<Tinh> getTinhs(){
        return tinhRepository.findAll();
    }
}
