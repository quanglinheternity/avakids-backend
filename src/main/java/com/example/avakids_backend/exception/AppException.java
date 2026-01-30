package com.example.avakids_backend.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AppException extends RuntimeException {

    private ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        super(errorCode.name());
        this.errorCode = errorCode;
    }
}
