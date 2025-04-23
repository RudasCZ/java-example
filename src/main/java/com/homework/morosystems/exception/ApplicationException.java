package com.homework.morosystems.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class ApplicationException extends RuntimeException  {
    private final String errorMsg;
    private final HttpStatus httpStatus;

    @Override
    public final synchronized String toString() {
        return "{httpStatus=%s, errorMsg=%s}"
                .formatted(this.httpStatus != null ? this.httpStatus.value() : null, errorMsg);
    }

    @Override
    public String getMessage() {
        return this.toString();
    }
}
