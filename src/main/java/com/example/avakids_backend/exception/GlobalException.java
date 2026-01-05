package com.example.avakids_backend.exception;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.avakids_backend.DTO.ApiResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalException {
    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException e) {
        log.error("error", e);
        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_ERROR;
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    // @ExceptionHandler(AuthorizationDeniedException.class)
    // public ResponseEntity<ApiResponse<?>> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
    //     ApiResponse<?> response = ApiResponse.builder()
    //             .code(ErrorCode.UNAUTHORIZED.getCode())
    //             .message(ErrorCode.UNAUTHORIZED.getMessage())
    //             .build();
    //     return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    // }
    // @ExceptionHandler(HttpMessageNotReadableException.class)
    // public ResponseEntity<Map<String, Object>> handleEnumParseError(HttpMessageNotReadableException ex) {
    //     Map<String, Object> body = new HashMap<>();
    //     body.put("code", 400);
    //     body.put("message", "Dữ liệu không hợp lệ");

    //     // Thông báo lỗi chi tiết cho enum
    //     Map<String, String> errors = new HashMap<>();
    //     if (ex.getMessage().contains("TrangThaiTaiXe")) {
    //         errors.put("trangThaiLamViec", "Giá trị không hợp lệ. Các giá trị hợp lệ: RANH, DANG_CHAY, NGHI_PHEP,
    // TAM_KHOA");
    //     }
    //     body.put("errors", errors);

    //     return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    // }
    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<Void>> hanldingAccessDeniedException(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleEnumParseError(HttpMessageNotReadableException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", 400);
        body.put("message", "Dữ liệu không hợp lệ");

        Map<String, String> errors = new HashMap<>();

        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException invalidFormatException) {
            // Lấy tên field bị lỗi
            String fieldName = invalidFormatException.getPath().stream()
                    .map(ref -> ref.getFieldName() != null ? ref.getFieldName() : ref.getIndex() + "")
                    .collect(Collectors.joining("."));

            String invalidValue = invalidFormatException.getValue().toString();
            Class<?> targetType = invalidFormatException.getTargetType();

            if (targetType != null && targetType.isEnum()) {
                // Lấy danh sách giá trị hợp lệ của enum
                String validValues = Arrays.stream(targetType.getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));

                String errorMsg =
                        String.format("Giá trị '%s' không hợp lệ. Các giá trị hợp lệ: %s", invalidValue, validValues);

                errors.put(fieldName, errorMsg);
            } else {
                errors.put(fieldName, "Giá trị không hợp lệ: " + invalidValue);
            }
        } else {
            // Trường hợp không phải lỗi enum
            errors.put("request", "Dữ liệu JSON không đúng định dạng");
        }

        body.put("errors", errors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<Void>> handleAppException(AppException appException) {
        ErrorCode errorCode = appException.getErrorCode();
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(response);
    }

    // @ExceptionHandler(MethodArgumentNotValidException.class)
    // public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException exception) {
    //     List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();

    //     // Gom tất cả lỗi thành danh sách errorCode
    //     List<ErrorCode> errors = fieldErrors.stream()
    //             .map(fieldError -> {
    //                 String enumKey = fieldError.getDefaultMessage();
    //                 try {
    //                     return ErrorCode.valueOf(enumKey);
    //                 } catch (Exception e) {
    //                     return ErrorCode.INVALID_REQUEST;
    //                 }
    //             })
    //             .toList();

    //     // Trả về danh sách lỗi
    //     Map<String, Object> errorResponse = new HashMap<>();
    //     errorResponse.put(
    //             "errors",
    //             errors.stream()
    //                     .map(err -> Map.of(
    //                             "code", err.getCode(),
    //                             "message", err.getMessage()))
    //                     .toList());

    //     ApiResponse<Object> apiResponse = ApiResponse.<Object>builder()
    //             .code(ErrorCode.INVALID_REQUEST.getCode())
    //             .message(ErrorCode.INVALID_REQUEST.getMessage())
    //             .data(errorResponse)
    //             .build();

    //     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    // }
    // Xử lý lỗi do @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String code = error.getDefaultMessage();
            String message;
            if ("nhomChiPhi".equals(field)) {
                // Nếu lỗi là enum TrangThaiTaiXe
                message =
                        "Nhóm chi phí không hợp lệ. Giá trị hợp lệ: NHIEN_LIEU, PHI_DUONG_BO, SINH_HOAT, BAO_DUONG, PHAT_SINH";
            } else {
                // Các lỗi khác vẫn dùng ErrorCode cũ
                ErrorCode errorCode = Arrays.stream(ErrorCode.values())
                        .filter(e -> e.name().equals(code))
                        .findFirst()
                        .orElse(ErrorCode.UNCATEGORIZED_ERROR);
                message = errorCode.getMessage();
            }

            errors.put(field, message);
        });

        response.put("code", 400);
        response.put("message", "Dữ liệu không hợp lệ");
        response.put("errors", errors);

        return ResponseEntity.badRequest().body(response);
    }
}
