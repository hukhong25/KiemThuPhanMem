package com.bookstore.book_sell_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "TAC_GIA")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class TacGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maTG;

    private String tenTG;

    @ManyToMany(mappedBy = "tacGiaSet")
    @JsonIgnore
    private Set<Sach> sachSet;
}
