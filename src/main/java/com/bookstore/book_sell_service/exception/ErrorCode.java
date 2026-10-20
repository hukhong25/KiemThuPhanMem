package com.bookstore.book_sell_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_ERROR(999,"uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_EXISTED(101,"user existed",HttpStatus.BAD_REQUEST),
    INVALID_USERNAME(102,"username must not be blank or has special characters",HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(103,"password must be at least 8 characters",HttpStatus.BAD_REQUEST),
    INVALID_KEY(104,"invalid message key",HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(105,"user not existed",HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(106,"unauthenticated",HttpStatus.UNAUTHORIZED),
    BLANK_PASSWORD(107,"password must not be blank",HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(108,"invalid email",HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(109,"you do not have permission",HttpStatus.FORBIDDEN),
    DUPLICATE_ENTRY(110, "username, email or SĐT has been used", HttpStatus.BAD_REQUEST),
    INVALID_ARGUMENT(111,"Trang thai khong ton tai",HttpStatus.BAD_REQUEST)
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
