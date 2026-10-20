package com.bookstore.book_sell_service.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SearchSyncService {

    public void syncSach(Long maSach) {
        log.info("syncSach: {}", maSach);
    }

    public void deleteSachFromIndex(Long maSach) {
        log.info("deleteSachFromIndex: {}", maSach);
    }
}