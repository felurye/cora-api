package com.minibank.api.exception;

import com.minibank.api.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateCpfException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateCpf(
            DuplicateCpfException ex,
            HttpServletRequest request
    ) {
        ErrorResponse body = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(CouponNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCouponNotFound(
            CouponNotFoundException ex,
            HttpServletRequest request
    ) {
        ErrorResponse body = ErrorResponse.builder()
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .error(HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
    }

    @ExceptionHandler(CouponLimitReachedException.class)
    public ResponseEntity<ErrorResponse> handleCouponLimitReached(
            CouponLimitReachedException ex,
            HttpServletRequest request
    ) {
        ErrorResponse body = ErrorResponse.builder()
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .error(HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLocking(
            ObjectOptimisticLockingFailureException ex,
            HttpServletRequest request
    ) {
        ErrorResponse body = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message("The coupon was updated by another request at the same time. Please try again.")
                .path(request.getRequestURI())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<ErrorResponse.FieldErrorDetail> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> new ErrorResponse.FieldErrorDetail(
                        fieldError.getField(),
                        fieldError.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        ErrorResponse body = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation error in request body.")
                .path(request.getRequestURI())
                .timestamp(Instant.now())
                .errors(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}

