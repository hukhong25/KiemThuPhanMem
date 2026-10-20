package com.bookstore.book_sell_service.dto.FetchTinhHuyen;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DistrictFullApiResponse {
    @JsonProperty("code")
    private int code;
    
    @JsonProperty("name")
    private String name;

    @JsonProperty("province_code")
    private int provinceCode;

}
