package com.bookstore.book_sell_service.specification;


import com.bookstore.book_sell_service.entity.Sach;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class SachSpecification {
    // ây dựng điều kiện truy vấn động
    public static Specification<Sach> filterByPrice(Double minPrice, Double maxPrice, String orderBy, String order) {
        return (root, query, cb) -> {

            Predicate predicate = cb.conjunction(); // tao 1 dieu kien luon dung
            if (minPrice != null && maxPrice != null) {
                predicate = cb.and(predicate, cb.between(root.get(orderBy), minPrice, maxPrice));
            }
            // cb = “nhà máy chế tạo các thành phần nhỏ”
//            query = “bản thiết kế tổng thể của câu SQL”
            if (order != null) {
                if (order.equalsIgnoreCase("asc")) {
                    query.orderBy(cb.asc(root.get(orderBy)));
                } else if (order.equalsIgnoreCase("desc")) {
                    query.orderBy(cb.desc(root.get(orderBy)));
                }
            }
            return predicate;
        };

    }
}
