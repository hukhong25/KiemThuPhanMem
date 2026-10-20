package com.bookstore.book_sell_service.services;


import com.bookstore.book_sell_service.dto.FetchTinhHuyen.DistrictFullApiResponse;
import com.bookstore.book_sell_service.dto.FetchTinhHuyen.ProvinceApiResponse;
import com.bookstore.book_sell_service.entity.QuanHuyen;
import com.bookstore.book_sell_service.entity.Tinh;
import com.bookstore.book_sell_service.repositories.QuanHuyenRepository;
import com.bookstore.book_sell_service.repositories.TinhRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProvinceDataInitService implements CommandLineRunner {

    private final TinhRepository tinhRepository;
    private final QuanHuyenRepository quanHuyenRepository;
    private final ObjectMapper objectMapper;

    private static final String PROVINCE_API_URL = "https://provinces.open-api.vn/api/p/";
    private static final String DISTRICT_API_URL = "https://provinces.open-api.vn/api/d/";

    @Override
    public void run(String... args) throws Exception {
        // Kiểm tra nếu đã có dữ liệu thì không fetch lại
        long existingTinhCount = tinhRepository.count();
        long existingHuyenCount = quanHuyenRepository.count();

        log.info("=== KIỂM TRA DỮ LIỆU BAN ĐẦU ===");
        log.info("Số lượng tỉnh hiện có: {}", existingTinhCount);
        log.info("Số lượng quận/huyện hiện có: {}", existingHuyenCount);

        if (existingTinhCount > 0 && existingHuyenCount > 0) {
            log.info("existed quan huyen, tinh. skipping fetch from API.");
            return;
        }

        log.info("=== BẮT ĐẦU FETCH DỮ LIỆU TỪ API ===");

        try {
            // Bước 1: Fetch và lưu tỉnh
            List<ProvinceApiResponse> provinces = fetchProvinces();
            if (provinces == null || provinces.isEmpty()) {
                log.error(" Không thể lấy dữ liệu tỉnh từ API");
                return;
            }

            // Bước 2: Lưu tỉnh vào database và tạo map code -> tỉnh
            Map<Integer, Tinh> provinceCodeMap = saveProvinces(provinces);

            // Bước 3: Fetch và lưu quận/huyện
            List<DistrictFullApiResponse> districts = fetchDistricts();
            if (districts == null || districts.isEmpty()) {
                log.error(" Không thể lấy dữ liệu quận/huyện từ API");
                return;
            }

            // Bước 4: Lưu quận/huyện
            saveDistricts(districts, provinceCodeMap);

            // Verify data after save
            long tinhCount = tinhRepository.count();
            long huyenCount = quanHuyenRepository.count();
            log.info("=== KẾT QUẢ SAU KHI LƯU ===");
            log.info(" Tổng số tỉnh trong DB: {}", tinhCount);
            log.info(" Tổng số quận/huyện trong DB: {}", huyenCount);

        } catch (Exception e) {
            log.error(" Lỗi khi fetch dữ liệu từ API: {}", e.getMessage(), e);
        }
    }

    private List<ProvinceApiResponse> fetchProvinces() {
        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .build();

            JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
            RestClient restClient = RestClient.builder()
                    .requestFactory(requestFactory)
                    .build();

            log.info(" Đang gọi API Tỉnh: {}", PROVINCE_API_URL);

            String responseBody = restClient.get()
                    .uri(PROVINCE_API_URL)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("User-Agent", "Mozilla/5.0")
                    .retrieve()
                    .body(String.class);

            if (responseBody == null || responseBody.trim().isEmpty()) {
                log.error(" API tỉnh trả về response rỗng");
                return null;
            }

            if (responseBody.trim().startsWith("<")) {
                log.error(" API tỉnh trả về HTML thay vì JSON");
                return null;
            }

            log.info("Đã nhận response từ API Tỉnh ({} ký tự)", responseBody.length());

            List<ProvinceApiResponse> provinces = objectMapper.readValue(
                    responseBody,
                    new TypeReference<List<ProvinceApiResponse>>() {}
            );

            log.info("sucessful parse {} tinh", provinces.size());
            return provinces;

        } catch (Exception e) {
            log.error(" unable to fetch tinh: {}", e.getMessage(), e);
            return null;
        }
    }

    private List<DistrictFullApiResponse> fetchDistricts() {
        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .build();

            JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
            RestClient restClient = RestClient.builder()
                    .requestFactory(requestFactory)
                    .build();

            log.info(" Calling API Quận/Huyện: {}", DISTRICT_API_URL);

            String responseBody = restClient.get()
                    .uri(DISTRICT_API_URL)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("User-Agent", "Mozilla/5.0")
                    .retrieve()
                    .body(String.class);

            if (responseBody == null || responseBody.trim().isEmpty()) {
                log.error(" API quận/huyện trả về response rỗng");
                return null;
            }

            if (responseBody.trim().startsWith("<")) {
                log.error(" API quận/huyện trả về HTML thay vì JSON");
                return null;
            }

            log.info(" Đã nhận response từ API Quận/Huyện ({} ký tự)", responseBody.length());

            List<DistrictFullApiResponse> districts = objectMapper.readValue(
                    responseBody,
                    new TypeReference<List<DistrictFullApiResponse>>() {}
            );

            log.info("Parse thành công {} quận/huyện", districts.size());
            return districts;

        } catch (Exception e) {
            log.error(" Lỗi khi fetch quận/huyện: {}", e.getMessage(), e);
            return null;
        }
    }

    @Transactional
    public Map<Integer, Tinh> saveProvinces(List<ProvinceApiResponse> provinces) {
        log.info(" Bắt đầu lưu {} tỉnh vào database...", provinces.size());

        Map<Integer, Tinh> provinceCodeMap = new HashMap<>();
        int savedCount = 0;

        for (ProvinceApiResponse provinceApi : provinces) {
            try {
                if (provinceApi.getName() == null || provinceApi.getName().trim().isEmpty()) {
                    continue;
                }

                Tinh tinh = Tinh.builder()
                        .tenTinh(provinceApi.getName())
                        .quanHuyenList(new ArrayList<>())
                        .build();

                tinh = tinhRepository.save(tinh);
                savedCount++;

                // Lưu vào map để sau này mapping với quận/huyện
                provinceCodeMap.put(provinceApi.getCode(), tinh);

                log.info(" [{}/{}] Lưu tỉnh: {} (code: {}, ID: {})",
                        savedCount, provinces.size(),
                        tinh.getTenTinh(), provinceApi.getCode(), tinh.getMaTinh());

            } catch (Exception e) {
                log.error(" Lỗi khi lưu tỉnh {}: {}", provinceApi.getName(), e.getMessage());
            }
        }

        tinhRepository.flush();
        log.info(" Đã lưu {} tỉnh", savedCount);
        return provinceCodeMap;
    }

    @Transactional
    public void saveDistricts(List<DistrictFullApiResponse> districts,
                              Map<Integer, Tinh> provinceCodeMap) {
        log.info(" Bắt đầu lưu {} quận/huyện vào database...", districts.size());

        int savedCount = 0;
        int skippedCount = 0;

        for (DistrictFullApiResponse districtApi : districts) {
            try {
                if (districtApi.getName() == null || districtApi.getName().trim().isEmpty()) {
                    skippedCount++;
                    continue;
                }

                // Tìm tỉnh tương ứng
                Tinh tinh = provinceCodeMap.get(districtApi.getProvinceCode());
                if (tinh == null) {
                    log.warn(" Không tìm thấy tỉnh với code {} cho quận/huyện {}",
                            districtApi.getProvinceCode(), districtApi.getName());
                    skippedCount++;
                    continue;
                }

                QuanHuyen quanHuyen = QuanHuyen.builder()
                        .tenQuanHuyen(districtApi.getName())
                        .tinh(tinh)
                        .build();

                quanHuyenRepository.save(quanHuyen);
                savedCount++;

                if (savedCount <= 10 || savedCount % 100 == 0) {
                    log.info(" [{}/{}] Lưu quận/huyện: {} (thuộc {})",
                            savedCount, districts.size(),
                            quanHuyen.getTenQuanHuyen(), tinh.getTenTinh());
                }

            } catch (Exception e) {
                log.error(" Lỗi khi lưu quận/huyện {}: {}",
                        districtApi.getName(), e.getMessage());
                skippedCount++;
            }
        }

        quanHuyenRepository.flush();
        log.info("=== TỔNG KẾT QUẬN/HUYỆN ===");
        log.info("Đã lưu: {}", savedCount);
        log.info("Bỏ qua: {}", skippedCount);
    }
}