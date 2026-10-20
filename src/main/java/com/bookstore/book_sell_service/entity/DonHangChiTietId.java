package com.bookstore.book_sell_service.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
@Data
@Embeddable
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DonHangChiTietId implements Serializable {
    private Long maDonHang;
    private Long maSach;


}
