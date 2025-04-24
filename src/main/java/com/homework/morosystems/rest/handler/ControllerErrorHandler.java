package com.homework.morosystems.rest.handler;

import com.homework.morosystems.exception.ApplicationException;
import com.homework.morosystems.mapper.ErrorMapper;
import com.homework.morosystems.model.ErrorResponseDto;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
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

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUsernameNotFoundException(UsernameNotFoundException e) {
        log.warn("UsernameNotFoundException occurred while processing request. {}", e.toString(), e);
        ErrorResponseDto error = new ErrorResponseDto().status(HttpStatus.UNAUTHORIZED.toString()).message("Unauthorized");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(ValidationException e) {
        log.error("ValidationException occurred.", e);
        ErrorResponseDto error = new ErrorResponseDto().status(HttpStatus.BAD_REQUEST.toString()).message("Bad request");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    public ResponseEntity<ErrorResponseDto> handleServletRequestBindingException(ServletRequestBindingException e) {
        log.error("ServletRequestBindingException occurred.", e);
        ErrorResponseDto error = new ErrorResponseDto().status(HttpStatus.BAD_REQUEST.toString()).message("Bad request");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDto> handleRuntimeException(RuntimeException e) {
        log.error("Runtime exception occurred.", e);
        ErrorResponseDto error = new ErrorResponseDto().status(HttpStatus.INTERNAL_SERVER_ERROR.toString()).message("Internal server runtime error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
