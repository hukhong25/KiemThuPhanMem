package com.bookstore.book_sell_service.dto.FetchTinhHuyen;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ProvinceApiResponse {
    @JsonProperty("code")
    private int code;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("districts")
    private List<DistrictFullApiResponse> districts;
}
