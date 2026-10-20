package com.bookstore.book_sell_service.repositories;

import com.bookstore.book_sell_service.entity.Tinh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface TinhRepository extends JpaRepository<Tinh,Long> {
    Optional<Tinh> findByTenTinh(String tenTinh);
}
