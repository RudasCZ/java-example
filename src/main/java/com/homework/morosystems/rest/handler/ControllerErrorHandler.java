package com.homework.morosystems.rest.handler;

import com.homework.morosystems.exception.ApplicationException;
import com.homework.morosystems.mapper.ErrorMapper;
import com.homework.morosystems.model.ErrorResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
@AllArgsConstructor
public class ControllerErrorHandler {

    private final ErrorMapper errorMapper;

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(ApplicationException e) {
        log.warn("ApplicationException occurred while processing request. {}", e.toString(), e);
        ErrorResponseDto error = errorMapper.toErrorResponse(e);
        return ResponseEntity.status(e.getHttpStatus()).body(error);
    }
}
