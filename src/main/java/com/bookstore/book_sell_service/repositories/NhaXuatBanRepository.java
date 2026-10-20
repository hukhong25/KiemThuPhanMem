package com.bookstore.book_sell_service.repositories;

import com.bookstore.book_sell_service.entity.NhaXuatBan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface NhaXuatBanRepository extends JpaRepository<NhaXuatBan,Long> {
    Optional<NhaXuatBan> findByTenNXB(String tenNXB);
}
