package com.bookstore.book_sell_service.repositories;

import com.bookstore.book_sell_service.entity.InvalidateToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidatedTokenRepository extends JpaRepository<InvalidateToken, String> {
}
