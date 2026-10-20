package com.bookstore.book_sell_service.repositories;

import com.bookstore.book_sell_service.dto.responses.ThongKeSach;
import com.bookstore.book_sell_service.entity.Sach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SachRepository extends JpaRepository<Sach,Long>, JpaSpecificationExecutor<Sach> {

    // sach co so luong ton kho thap
    @Query("SELECT new com.bookstore.book_sell_service.dto.responses.ThongKeSach(" +
            "ct.tenSach, ct.soLuongCo) " +
            "FROM Sach ct " +
            "WHERE ct.soLuongCo < 20")
    List<ThongKeSach> thongKeTKT();

    List<Sach> findAllByLoaiSach_MaLoai(Long maLoai);

    boolean existsByTenSach(String tenSach);
}
