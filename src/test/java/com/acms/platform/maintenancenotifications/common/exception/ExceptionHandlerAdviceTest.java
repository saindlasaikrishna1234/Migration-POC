package com.acms.platform.maintenancenotifications.common.exception;

import com.acms.platform.common.core.exception.handler.Error;
import com.acms.platform.common.core.exception.handler.Errors;
import com.acms.platform.maintenancenotifications.common.exception.errorcodes.ServerErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ExceptionHandlerAdviceTest {

    @InjectMocks
    private ExceptionHandlerAdvice exceptionHandlerAdvice;

    @Test
    void givenErrorCodeException_handleThrowable_shouldFormatError() {
        ReflectionTestUtils.setField(exceptionHandlerAdvice, "space", "dev");
        ErrorCodeException exception = ErrorCodeException.buildErrorCodeException(ServerErrorCode.SERVER_UNEXPECTED_ERROR);


        ResponseEntity<Errors> output = exceptionHandlerAdvice.handleThrowable(exception);


        Error error = output.getBody().getErrors().get(0);
        assertThat(error.getCode()).isEqualTo("500000");
        assertThat(error.getTitle()).isEqualTo("Unexpected error");
        assertThat(error.getDetail()).isEqualTo("An unexpected error has occurred");
        assertThat(error.getStatus()).isEqualTo("Internal Server Error");
    }
}