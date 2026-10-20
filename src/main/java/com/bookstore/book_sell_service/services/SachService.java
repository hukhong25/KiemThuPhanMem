package com.bookstore.book_sell_service.services;

import com.bookstore.book_sell_service.dto.request.Sach.SachCreationalRequest;
import com.bookstore.book_sell_service.dto.request.Sach.SachFilterRequest;
import com.bookstore.book_sell_service.dto.request.Sach.SachUpdateRequest;
import com.bookstore.book_sell_service.dto.responses.SachResponse;
import com.bookstore.book_sell_service.entity.NhaXuatBan;
import com.bookstore.book_sell_service.entity.Sach;
import com.bookstore.book_sell_service.entity.TacGia;
import com.bookstore.book_sell_service.mapper.SachMapper;
import com.bookstore.book_sell_service.mapper.TacGiaMapper;
import com.bookstore.book_sell_service.repositories.*;
import com.bookstore.book_sell_service.specification.SachSpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SachService {
    private final TacGiaMapper tacGiaMapper;
    SachRepository sachRepository;
    SachMapper sachMapper;
    TacGiaRepository tacGiaRepository;
    TheLoaiRepository theLoaiRepository;
    NhaXuatBanRepository nhaXuatBanRepository;
    SearchSyncService searchSyncService;
    DanhGiaRepository danhGiaRepository;
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public SachResponse createSach(SachCreationalRequest request){
        if(sachRepository.existsByTenSach(request.getTenSach())){
            throw new RuntimeException("Da co sach nay");
        }

        Sach sach = sachMapper.toSach(request);
        NhaXuatBan nhaXuatBan = nhaXuatBanRepository.findById(request.getNhaXuatBan().getMaNXB())
                .orElseThrow(()-> new RuntimeException("Khong thay ma loai"));


        sach.setLoaiSach(theLoaiRepository.findById(request.getLoaiSach().getMaLoai())
                .orElseThrow(()-> new RuntimeException("Khong thay ma loai")));

        sach.setNhaXuatBan(nhaXuatBan);
        List<TacGia> tacGiaSet = tacGiaRepository.findAllById(request.getTacGiaIds());
        sach.setTacGiaSet(new HashSet<>(tacGiaSet));

        Sach savedSach = sachRepository.save(sach);
        searchSyncService.syncSach(savedSach.getMaSach());
        return sachMapper.toSachResponse(savedSach);

    }

    // Lấy tất cả sách (không lọc) và convert sang Response
    public List<SachResponse> getAllSachs() {
        // sachRepository.findAll() là hàm có sẵn của JPA
        List<Sach> sachList = sachRepository.findAll();
        // Convert list Entity sang list Response bằng Mapper
        return sachList.stream()
                .map( sach -> {
                            SachResponse res = sachMapper.toSachResponse(sach);
                            Long sumDG = danhGiaRepository.tongSoDanhGiaOfSach(sach.getMaSach());
                            res.setSoLuotDG(sumDG);
                            if (sumDG > 0 ){
                                Float avgSao = (float) danhGiaRepository.tongSoSaoOfSach(sach.getMaSach())/ sumDG;
                                res.setAvgSao(avgSao);
                            }
                            return  res;
                        }

                )
                .collect(Collectors.toList());
    }

    // get all sach theo khoang gia hoac sap xep theo gia giam dan hay tang dan

    public List<SachResponse> getAllSachsByPrice(SachFilterRequest request){
        List<Sach> sachList = sachRepository.findAll
                (SachSpecification.filterByPrice(request.getMinPrice(),request.getMaxPrice(),request.getOrderBy(),request.getOrder()));

        return sachMapper.toListSachResponse(sachList);
    }


    public SachResponse getSach (@PathVariable  Long maSach){
        SachResponse sachResponse =  sachMapper.toSachResponse(sachRepository.findById(maSach)
                .orElseThrow(()->new RuntimeException("book not found")));
        Long sumDG = danhGiaRepository.tongSoDanhGiaOfSach(maSach);
        sachResponse.setSoLuotDG(sumDG);
        if (sumDG > 0 ){
            Float avgSao = (float) danhGiaRepository.tongSoSaoOfSach(maSach)/ sumDG;
            sachResponse.setAvgSao(avgSao);
        }
       return sachResponse;
    }

    public SachResponse updateSach(SachUpdateRequest request, Long maSach){
        Sach sach = sachRepository.findById(maSach)
                .orElseThrow(()-> new RuntimeException("book not found"));

        sachMapper.updateSach(sach, request);

        NhaXuatBan nhaXuatBan = nhaXuatBanRepository.findById(request.getNhaXuatBan().getMaNXB())
                .orElseThrow(()-> new RuntimeException("Khong thay ma loai"));

        sach.setLoaiSach(theLoaiRepository.findById(request.getLoaiSach().getMaLoai())
                .orElseThrow(()-> new RuntimeException("Khong thay ma loai")));

        sach.setNhaXuatBan(nhaXuatBan);


        List<TacGia> tacGiaSet = tacGiaRepository.findAllById(request.getTacGiaIds());
        sach.setTacGiaSet(new HashSet<>(tacGiaSet));

        return sachMapper.toSachResponse(sachRepository.save(sach));
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public void deleteSach(Long maSach){
        sachRepository.deleteById(maSach);
        searchSyncService.deleteSachFromIndex(maSach);
    }

    public List<SachResponse> getSachsByLoai(Long maLoai) {
        // Gọi repository để lấy list Entity
        List<Sach> sachList = sachRepository.findAllByLoaiSach_MaLoai(maLoai);

        // Convert list Entity sang list Response bằng Mapper
        return sachList.stream()
                .map(sachMapper::toSachResponse)
                .collect(Collectors.toList());
    }
}
