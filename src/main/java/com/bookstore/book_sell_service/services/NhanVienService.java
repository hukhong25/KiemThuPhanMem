package com.bookstore.book_sell_service.services;

import com.bookstore.book_sell_service.dto.request.NhanVien.NhanVienCreationalRequest;
import com.bookstore.book_sell_service.dto.request.NhanVien.NhanVienUpdateRequest;
import com.bookstore.book_sell_service.dto.responses.KHResponse;
import com.bookstore.book_sell_service.dto.responses.NVResponse;
import com.bookstore.book_sell_service.entity.KhachHang;
import com.bookstore.book_sell_service.entity.NhanVien;
import com.bookstore.book_sell_service.enums.Role;
import com.bookstore.book_sell_service.exception.AppException;
import com.bookstore.book_sell_service.exception.ErrorCode;
import com.bookstore.book_sell_service.mapper.NhanVienMapper;
import com.bookstore.book_sell_service.repositories.NhanVienRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class NhanVienService {
    NhanVienRepository nhanVienRepository;
    NhanVienMapper nhanVienMapper;
    PasswordEncoder passwordEncoder;

    @PreAuthorize("hasRole('ADMIN')")
    public NVResponse createNhanVien(NhanVienCreationalRequest request){
        if(nhanVienRepository.existsByTenDangNhap(request.getTenDangNhap())){
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        NhanVien nhanVien = nhanVienMapper.toNhanVien(request);
        nhanVien.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
        HashSet<String> roles=new HashSet<>();
        roles.add(Role.STAFF.name());
        roles.add(Role.USER.name());
        nhanVien.setRoles(roles);
        return nhanVienMapper.toNhanVienResponse(nhanVienRepository.save(nhanVien));
    }
    @PostAuthorize("returnObject.tenDangNhap == authentication.name")
    public NVResponse getNhanVien(Long maNV){
        return nhanVienMapper.toNhanVienResponse(nhanVienRepository.findById(maNV)
                .orElseThrow(()-> new RuntimeException("user not found")));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<NVResponse> getAllNhanViens(){
        return nhanVienRepository.findAll().stream().
                map(nhanVienMapper::toNhanVienResponse).collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostAuthorize("returnObject.tenDangNhap == authentication.name")
    public NVResponse updateNhanVien(Long maNV,NhanVienUpdateRequest request){
        NhanVien nhanVien= nhanVienRepository.findById(maNV)
                .orElseThrow(()-> new RuntimeException("user not found"));
        nhanVienMapper.updateNhanVien(nhanVien,request);
        nhanVien.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
        return nhanVienMapper.toNhanVienResponse(nhanVienRepository.save(nhanVien)) ;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteNhanVien(Long maNV) {
        nhanVienRepository.deleteById(maNV);
    }

    public NVResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        NhanVien nhanVien = nhanVienRepository.findByTenDangNhap(name).orElseThrow(
                ()-> new AppException(ErrorCode.USER_NOT_EXISTED));
        return nhanVienMapper.toNhanVienResponse(nhanVien);
    }
}
