package com.minibank.api.exception;

import com.minibank.api.response.ErrorResponse;
import com.minibank.domain.entities.Coupon;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        when(request.getRequestURI()).thenReturn("/accounts");
    }

    @Test
    @DisplayName("Should return 409 when CPF is already registered")
    void handleDuplicateCpf_returns409() {
        DuplicateCpfException ex = new DuplicateCpfException("12345678901");

        ResponseEntity<ErrorResponse> response = handler.handleDuplicateCpf(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(409);
        assertThat(response.getBody().getMessage()).contains("12345678901");
        assertThat(response.getBody().getPath()).isEqualTo("/accounts");
    }

    @Test
    @DisplayName("Should return 422 when coupon is not found")
    void handleCouponNotFound_returns422() {
        CouponNotFoundException ex = new CouponNotFoundException("CORA10");

        ResponseEntity<ErrorResponse> response = handler.handleCouponNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(422);
        assertThat(response.getBody().getMessage()).contains("CORA10").contains("not found");
        assertThat(response.getBody().getPath()).isEqualTo("/accounts");
    }

    @Test
    @DisplayName("Should return 422 when coupon usage limit is reached")
    void handleCouponLimitReached_returns422() {
        CouponLimitReachedException ex = new CouponLimitReachedException("CORA10");

        ResponseEntity<ErrorResponse> response = handler.handleCouponLimitReached(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(422);
        assertThat(response.getBody().getMessage()).contains("CORA10").contains("usage limit");
        assertThat(response.getBody().getPath()).isEqualTo("/accounts");
    }

    @Test
    @DisplayName("Should return 409 when optimistic locking conflict occurs")
    void handleOptimisticLocking_returns409() {
        ObjectOptimisticLockingFailureException ex =
                new ObjectOptimisticLockingFailureException(Coupon.class, 1);

        ResponseEntity<ErrorResponse> response = handler.handleOptimisticLocking(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(409);
        assertThat(response.getBody().getMessage()).contains("try again");
        assertThat(response.getBody().getPath()).isEqualTo("/accounts");
    }
}
