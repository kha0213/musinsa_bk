package com.yl.musinsa2.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 엔티티를 찾을 수 없는 경우
     */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)  // 404 Not Found
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException ex) {
        log.warn("엔티티 조회 실패: {}", ex.getMessage());
        
        return ErrorResponse.builder()
                .success(false)
                .message("요청한 리소스를 찾을 수 없습니다.")
                .details(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * 잘못된 상태 예외
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)  // 400 Bad Request
    public ErrorResponse handleIllegalStateException(IllegalStateException ex) {
        log.warn("잘못된 상태 요청: {}", ex.getMessage());
        
        return ErrorResponse.builder()
                .success(false)
                .message("잘못된 요청입니다.")
                .details(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * 유효성 검증 실패
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)  // 400 Bad Request
    public ValidationErrorResponse handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("유효성 검증 실패: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        return ValidationErrorResponse.builder()
                .success(false)
                .message("입력값 검증에 실패했습니다.")
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * 기타 모든 예외
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)  // 500 Internal Server Error
    public ErrorResponse handleGenericException(Exception ex) {
        log.error("예상치 못한 오류 발생", ex);
        
        return ErrorResponse.builder()
                .success(false)
                .message("서버 내부 오류가 발생했습니다.")
                .details("시스템 관리자에게 문의하시기 바랍니다.")
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    // 에러 응답 DTO들
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ErrorResponse {
        private boolean success;
        private String message;
        private String details;
        private LocalDateTime timestamp;
    }
    
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ValidationErrorResponse {
        private boolean success;
        private String message;
        private Map<String, String> errors;
        private LocalDateTime timestamp;
    }
}
