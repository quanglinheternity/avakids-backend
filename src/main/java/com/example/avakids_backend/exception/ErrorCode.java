package com.example.avakids_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // General Errors (1000-1099)
    UNCATEGORIZED_ERROR(1000, "Lỗi hệ thống, vui lòng thử lại sau", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REQUEST(1001, "Yêu cầu không hợp lệ", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(1002, "Bạn không có quyền truy cập", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(1003, "Quyền truy cập bị từ chối", HttpStatus.FORBIDDEN),
    INVALID_ENUM_VALUE(1004, "Giá trị không hợp lệ", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(1005, "Lỗi hệ thống", HttpStatus.INTERNAL_SERVER_ERROR),

    // Authentication Errors (2000-2099)
    AUTHENTICATION_REQUIRED(2001, "Vui lòng đăng nhập để tiếp tục", HttpStatus.UNAUTHORIZED),
    AUTHENTICATION_FAILED(2002, "Sai tài khoản hoặc mật khẩu", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(2003, "Phiên đăng nhập đã hết hạn", HttpStatus.UNAUTHORIZED),
    INVALID_USERNAME(2004, "Tài khoản không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(2005, "Mật khẩu không hợp lệ", HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED(2006, "Mật khẩu không được để trống", HttpStatus.BAD_REQUEST),

    // User-related Errors (3000-3099)
    // ===== USER – VALIDATION =====
    EMAIL_REQUIRED(3000, "Email không được để trống", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(3001, "Email không đúng định dạng", HttpStatus.BAD_REQUEST),

    PHONE_REQUIRED(3002, "Số điện thoại không được để trống", HttpStatus.BAD_REQUEST),
    PHONE_INVALID(3003, "Số điện thoại không hợp lệ", HttpStatus.BAD_REQUEST),

    PASSWORD_TOO_SHORT(3004, "Mật khẩu tối thiểu 6 ký tự", HttpStatus.BAD_REQUEST),

    FULLNAME_REQUIRED(3005, "Họ tên không được để trống", HttpStatus.BAD_REQUEST),
    // ===== USER – BUSINESS =====
    EMAIL_ALREADY_EXISTS(3100, "Email đã tồn tại trong hệ thống", HttpStatus.BAD_REQUEST),
    PHONE_ALREADY_EXISTS(3101, "Số điện thoại đã tồn tại trong hệ thống", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(3102, "Không tìm thấy người dùng.", HttpStatus.BAD_REQUEST),
    // File-related Errors (9200-9299)
    FILE_EMPTY(9201, "File không được để trống", HttpStatus.BAD_REQUEST),
    FILE_TOO_LARGE(9202, "File quá lớn. Kích thước tối đa: 5MB", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE(
            9203,
            "Loại file không được hỗ trợ. Chỉ chấp nhận: jpg, jpeg, png, pdf, doc, docx, xls, xlsx",
            HttpStatus.BAD_REQUEST),
    INVALID_FILE_NAME(9204, "Tên file không hợp lệ", HttpStatus.BAD_REQUEST),
    FILE_UPLOAD_FAILED(9205, "Upload file thất bại", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_NOT_FOUND(9206, "Không tìm thấy file", HttpStatus.NOT_FOUND),
    FILE_DELETE_FAILED(9207, "Xóa file thất bại", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_NOT_READABLE(9208, "File không thể đọc được", HttpStatus.BAD_REQUEST),
    FILE_NOT_IMAGE(9209, "Ảnh không hợp lệ. Chỉ chấp nhận jpg, jpeg, png.", HttpStatus.BAD_REQUEST);
    private final int code;
    private final String message;
    private final HttpStatusCode httpStatusCode;

    ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }
}
