package com.bookstore.book_sell_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tinh")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tinh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maTinh;

    private String tenTinh;

    @OneToMany(mappedBy = "tinh")
    @JsonIgnore
    private List<QuanHuyen> quanHuyenList;

}
