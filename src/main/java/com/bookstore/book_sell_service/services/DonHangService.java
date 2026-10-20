package com.bookstore.book_sell_service.services;

import com.bookstore.book_sell_service.dto.request.DonHang.DonHangCreate;
import com.bookstore.book_sell_service.dto.request.DonHang.UpdateTrangThai;
import com.bookstore.book_sell_service.dto.responses.DHResponse;
import com.bookstore.book_sell_service.dto.responses.KHResponse;
import com.bookstore.book_sell_service.entity.*;
import com.bookstore.book_sell_service.enums.TrangThai;
import com.bookstore.book_sell_service.mapper.DonHangMapper;
import com.bookstore.book_sell_service.mapper.UserMapper;
import com.bookstore.book_sell_service.repositories.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
public class DonHangService {

    AuthenticationService authenticationService;
    KhachHangRepository khachHangRepository;
    ChiTietGioHangRepository chiTietGioHangRepository;
    DonHangMapper donHangMapper;
    GiamGiaRepository giamGiaRepository;
    NhanVienRepository nhanVienRepository;
    QuanHuyenRepository quanHuyenRepository;
    PTTTRepository ptttRepository;
    DonHangRepository donHangRepository;
    ChiTietDHRepository chiTietDHRepository;
    private final GioHangRepository gioHangRepository;
    UserMapper userMapper;

    @Transactional // Thêm transaction để đảm bảo tính toàn vẹn dữ liệu
    public DHResponse createDonHang(DonHangCreate request) {

        KhachHang khachHang = authenticationService.khachHang();

        if (khachHang.getGioHang() == null) {
            throw new RuntimeException("Giỏ hàng rỗng");
        }

        List<ChiTietGioHang> chiTietGioHangList = khachHang.getGioHang().getChiTietGioHangList();
        if (chiTietGioHangList.isEmpty()) {
            throw new RuntimeException("Giỏ hàng rỗng");
        }

        // Tính tổng tiền
        double tongTien = chiTietGioHangList.stream()
                .mapToDouble(ct -> ct.getSoLuongMua() * ct.getSach().getDonGia())
                .sum();

        // Áp dụng giảm giá nếu có
        GiamGia giamGia = null;
        if (request.getMaGiamGia() != null) {
            giamGia = giamGiaRepository.findById(request.getMaGiamGia())
                    .orElseThrow(() -> new RuntimeException("Mã giảm giá không tồn tại")); // Nên ném lỗi rõ ràng

            if (giamGia != null) {
                LocalDate ngayHienTai = LocalDate.now(); // Hoặc LocalDate.now()

                // 1. Kiểm tra ngày hiệu lực
                boolean checkThoiGian = (ngayHienTai.isAfter(giamGia.getNgayBatDau()) || ngayHienTai.isEqual(giamGia.getNgayBatDau()))
                        && (ngayHienTai.isEqual(giamGia.getNgayKetThuc()) || ngayHienTai.isBefore(giamGia.getNgayKetThuc()));

                // 2. Kiểm tra điều kiện tổng tiền đơn hàng (Logic Mới)
                // Nếu donHangToiThieu là null thì coi như là 0 (luôn thỏa mãn)
                double minOrder = (giamGia.getDonHangToiThieu() == null) ? 0 : giamGia.getDonHangToiThieu();
                boolean checkTongTien = tongTien >= minOrder;

                if (checkThoiGian) {
                    if (checkTongTien) {
                        // Áp dụng giảm giá (Công thức hiện tại của bạn là giảm theo %)
                        tongTien = tongTien * (100 - giamGia.getChietKhau()) / 100;
                    } else {
                        // Tùy chọn: Ném lỗi nếu đơn hàng chưa đủ điều kiện
                        throw new RuntimeException("Đơn hàng chưa đạt giá trị tối thiểu: " + minOrder + " để dùng mã này.");
                    }
                } else {
                    throw new RuntimeException("Mã giảm giá đã hết hạn hoặc chưa bắt đầu.");
                }
            }
        }
        tongTien = tongTien + request.getPhiGiaoHang();

        // Tạo đơn hàng
        DonHang donHang = donHangMapper.toDonHang(request);

        donHang.setQuanHuyen(quanHuyenRepository.findById(request.getMaQuanHuyen())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quận huyện")));

        donHang.setPhuongThucThanhToan(ptttRepository.findById(request.getMaPTTT())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phương thức thanh toán")));

        donHang.setNgayDat(LocalDate.now());
        if (giamGia != null) {
            donHang.setGiamGia(giamGia);
        }

        donHang.setTrangThai(TrangThai.CHO_XAC_NHAN.getMoTa());
        donHang.setTongTien(tongTien);
        donHang.setNgayDat(LocalDate.now());

        // QUAN TRỌNG: Lưu đơn hàng chỉ 1 LẦN trước khi tạo chi tiết
        donHang = donHangRepository.save(donHang);

        // Tạo chi tiết đơn hàng
        List<DonHangChiTiet> chiTietDonHangList = new ArrayList<>();

        for (ChiTietGioHang ctgh : chiTietGioHangList) {
            DonHangChiTietId donHangChiTietId = new DonHangChiTietId(
                    donHang.getMaDonHang(),
                    ctgh.getId().getMaSach()
            );

            DonHangChiTiet donHangChiTiet = DonHangChiTiet.builder()
                    .id(donHangChiTietId)
                    .donHang(donHang) // Set reference đến đơn hàng
                    .sach(ctgh.getSach()) // Set reference đến sách
                    .soLuongMua(ctgh.getSoLuongMua())
                    .giaMua(ctgh.getSoLuongMua() * ctgh.getSach().getDonGia())
                    .build();

            chiTietDonHangList.add(donHangChiTiet);
        }

        // Lưu tất cả chi tiết đơn hàng
        chiTietDHRepository.saveAll(chiTietDonHangList);

        // Xóa giỏ hàng
        GioHang gioHang = khachHang.getGioHang();

        gioHang.getChiTietGioHangList().clear();
        chiTietGioHangRepository.deleteByGioHang_MaGioHang(gioHang.getMaGioHang());

        gioHangRepository.save(gioHang);
        return donHangMapper.togetDH(donHangRepository.save(donHang));
    }

    // lay danh sach cac don hang tu admin
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public List<DHResponse> getALLDonHang(){
        List<DonHang> donHangList = donHangRepository.findAll();

        return donHangList.stream()
                .map(ct -> {
                    KhachHang kh = ct.getKhachHang();
                    KHResponse khResponse = userMapper.toKHResponse(kh);

                    DHResponse dhResponse = donHangMapper.togetDH(ct);

//                    // Gán KHResponse vào DHResponse
//                    dhResponse.setKhResponse(khResponse);

                    return dhResponse;
                })
                .collect(Collectors.toList());
    }


    // xem chi tiet don hang
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public DHResponse getDonHang(Long maDH){
        DonHang donHang = donHangRepository.findById(maDH)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // Map KhachHang → KHResponse
        KhachHang kh = donHang.getKhachHang();
        KHResponse khResponse = userMapper.toKHResponse(kh);

        DHResponse dhResponse = donHangMapper.togetDH(donHang);

//        // Gán KHResponse vào DHResponse
//        dhResponse.setKhResponse(khResponse);

        return dhResponse;
    }

    // cap nhat trang thai don hang
    //@PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public void updateTrangThai(UpdateTrangThai updateTrangThai)
    {

        NhanVien nhanVien = authenticationService.nhanVien();

        DonHang donHang =  donHangRepository.findById(updateTrangThai.getMaDonHang())
                .orElseThrow(() -> new RuntimeException("Ko tim thay don hang"));

        TrangThai tt = TrangThai.valueOf(updateTrangThai.getTrangThai());
        donHang.setTrangThai(tt.getMoTa());
        donHang.setNhanVien(nhanVien);

        donHangRepository.save(donHang);
    }

    public void updateTrangThaiKH(UpdateTrangThai updateTrangThai)
    {

        KhachHang khachHang = authenticationService.khachHang();

        DonHang donHang =  donHangRepository.findById(updateTrangThai.getMaDonHang())
                .orElseThrow(() -> new RuntimeException("Ko tim thay don hang"));

        TrangThai tt = TrangThai.valueOf(updateTrangThai.getTrangThai());
        donHang.setTrangThai(tt.getMoTa());
//        donHang.setNhanVien(nhanVien);

        donHangRepository.save(donHang);
    }

    // lay danh sach don hang cua ban than
    // lay danh sach don hang cua khach hang
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public List<DHResponse> getDHofKH (){
        KhachHang khachHang = authenticationService.khachHang();
        List<DonHang> donHangList = donHangRepository.findAllByKhachHang_maKH(khachHang.getMaKH());

        return donHangList.stream()
                .map(ct -> {
                    KhachHang kh = ct.getKhachHang();
                    KHResponse khResponse = userMapper.toKHResponse(kh);

                    DHResponse dhResponse = donHangMapper.togetDH(ct);
//
//                    // Gán KHResponse vào DHResponse
//                    dhResponse.setKhResponse(khResponse);

                    return dhResponse;
                })
                .collect(Collectors.toList());
    }


}