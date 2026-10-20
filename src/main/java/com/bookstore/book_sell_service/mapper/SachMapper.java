package com.bookstore.book_sell_service.mapper;

import com.bookstore.book_sell_service.dto.request.Sach.SachCreationalRequest;
import com.bookstore.book_sell_service.dto.request.Sach.SachUpdateRequest;
import com.bookstore.book_sell_service.dto.responses.SachResponse;
import com.bookstore.book_sell_service.entity.HinhAnh;
import com.bookstore.book_sell_service.entity.Sach;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SachMapper {

    @Mapping(target = "hinhAnhs" , source = "hinhAnhList")
    SachResponse toSachResponse(Sach sach);

    default List<String> map(List<HinhAnh> hinhAnhList) {
      return hinhAnhList == null
              ? List.of()
              : hinhAnhList.stream()
                    .map(HinhAnh::getMaAnh)
                    .toList();
    }

    Sach toSach(SachCreationalRequest request);
    void updateSach(@MappingTarget Sach sach,SachUpdateRequest request);
    List<SachResponse> toListSachResponse (List<Sach>sach);
}
