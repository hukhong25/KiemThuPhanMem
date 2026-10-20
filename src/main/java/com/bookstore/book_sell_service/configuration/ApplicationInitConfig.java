package com.bookstore.book_sell_service.configuration;

import com.bookstore.book_sell_service.entity.*;
import com.bookstore.book_sell_service.enums.Role;
import com.bookstore.book_sell_service.repositories.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;
    TheLoaiRepository theLoaiRepository;
    NhaXuatBanRepository nhaXuatBanRepository;
    SachRepository sachRepository;
    NhanVienRepository nhanVienRepository;
    QuanHuyenRepository quanHuyenRepository;
    @Bean
    ApplicationRunner applicationRunner(KhachHangRepository khachHangRepository) {
        return args -> {
            if (khachHangRepository.findByUserName("admin").isEmpty()) {
                var roles = new HashSet<String>();
                roles.add(Role.ADMIN.name());
                KhachHang khachHang = KhachHang.builder()
                        .userName("admin")
                        .matKhau(passwordEncoder.encode("admin"))
                        .roles(roles)
                        .build();
                khachHangRepository.save(khachHang);
                log.warn("admin user has been created with password: admin");
            }
            // Tạo LoaiSach mẫu (hoặc lấy nếu đã có)
            LoaiSach loaiSach = theLoaiRepository.findByTenLoai("Tiểu Thuyết")
                    .orElseGet(() -> theLoaiRepository.saveAndFlush(
                            LoaiSach.builder()
                                    .tenLoai("Tiểu Thuyết")
                                    .moTa("NVT dep trai co gi sai")
                                    .build()
                    ));

            // Tạo NhaXuatBan mẫu (hoặc lấy nếu đã có)
            NhaXuatBan nxbVanHoc = nhaXuatBanRepository.findByTenNXB("NXB Văn Học")
                    .orElseGet(() -> nhaXuatBanRepository.saveAndFlush(
                            NhaXuatBan.builder().tenNXB("NXB Văn Học").build()
                    ));
            if (sachRepository.count() == 0) {
                log.info("start");



                // Dùng List để chứa 10 cuốn sách
                List<Sach> sachList = new ArrayList<>();
                for (int i = 1; i <= 10; i++) {
                    Sach sach = Sach.builder()
                            .tenSach("Cuốn Sách Tiểu Thuyết " + i)
                            .donGia(100000.0 + (i * 1000)) // Giá tăng dần
                            .donViTinh("cuốn")
                            .soLuongCo(50 + i) // Số lượng khác nhau
                            .moTa("Đây là mô tả cho cuốn sách thứ " + i)
                            .loaiSach(loaiSach) // Gán loại sách đã tạo
                            .nhaXuatBan(nxbVanHoc) // Gán NXB đã tạo
                            .build();

                    sachList.add(sach);
                }
                sachRepository.saveAll(sachList);
            }
            // 3. Tạo 5 Nhân Viên (Mới)
            // Kiểm tra bằng tenDangNhap của nhân viên đầu tiên
            if (nhanVienRepository.findByTenDangNhap("nhanvien1").isEmpty()) {
                log.info("Tạo dữ liệu mẫu cho Nhân Viên...");

                // Set quyền cho nhân viên
                Set<String> staffRoles = new HashSet<>();
                staffRoles.add(Role.STAFF.name());
                staffRoles.add(Role.USER.name()); // Nhân viên cũng là 1 user

                List<NhanVien> nhanVienList = new ArrayList<>();
                for (int i = 1; i <= 5; i++) {
                    NhanVien nhanVien = NhanVien.builder()
                            .tenDangNhap("nhanvien" + i)
                            .matKhau(passwordEncoder.encode("password123")) // Mật khẩu chung
                            .hoTen("Nhân Viên " + i)
                            .soCCCD("012345678" + i)
                            .roles(staffRoles)
                            .build();
                    nhanVienList.add(nhanVien);
                }
                nhanVienRepository.saveAll(nhanVienList);
                log.info("Đã tạo thành công {} nhân viên mẫu.", nhanVienList.size());
            }

            // 4. Tạo 5 Khách Hàng (Mới)
            // Kiểm tra bằng userName của khách hàng đầu tiên
            if (khachHangRepository.findByUserName("khachhang1").isEmpty()) {
                log.info("Tạo dữ liệu mẫu cho Khách Hàng...");

                // Set quyền cho khách hàng
                Set<String> userRoles = new HashSet<>();
                userRoles.add(Role.USER.name());

                // Lấy một QuanHuyen mẫu (ID=1) để gán cho khách hàng
                // Giả định ProvinceDataInitService đã chạy và tạo ra dữ liệu
                QuanHuyen qh = quanHuyenRepository.findById(1L).orElse(null);
                if (qh == null) {
                    log.warn("Không tìm thấy QuanHuyen mẫu (ID=1), khách hàng sẽ không có quận huyện.");
                }

                List<KhachHang> khachHangList = new ArrayList<>();
                for (int i = 1; i <= 5; i++) {
                    KhachHang khachHang = KhachHang.builder()
                            .userName("khachhang" + i)
                            .matKhau(passwordEncoder.encode("password123"))
                            .hoTen("Khách Hàng " + i)
                            .email("khachhang" + i + "@example.com")
                            .soDT("098765432" + i)
                            .diaChi("123 Đường " + i + ", Phường ABC")
                            .roles(userRoles)
                            .quanHuyen(qh) // Gán quận huyện mẫu
                            .build();
                    khachHangList.add(khachHang);
                }
                khachHangRepository.saveAll(khachHangList);
            }

        };
    }
    }
