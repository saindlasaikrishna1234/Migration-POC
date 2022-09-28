package com.acms.platform.maintenancenotifications.common.exception;

import com.acms.platform.common.core.exception.handler.BaseExceptionHandlerAdvice;
import com.acms.platform.common.core.exception.handler.Error;
import com.acms.platform.common.core.exception.handler.ErrorBuilder;
import com.acms.platform.common.core.exception.handler.Errors;
import com.acms.platform.maintenancenotifications.common.exception.errorcodes.ClientErrorCode;
import com.aic.framework.vehicleidentity.exceptions.*;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.Map;
import java.util.ResourceBundle;

import static com.acms.platform.maintenancenotifications.common.exception.ErrorCodeException.buildErrorCodeException;
import static com.acms.platform.maintenancenotifications.common.exception.errorcodes.ClientErrorCode.ACCESS_DENIED;
import static com.acms.platform.maintenancenotifications.common.exception.errorcodes.ClientErrorCode.MANDATORY_INPUT_MISSING;
import static com.acms.platform.maintenancenotifications.common.exception.errorcodes.ServerErrorCode.SERVER_UNEXPECTED_ERROR;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@ControllerAdvice
public class ExceptionHandlerAdvice extends BaseExceptionHandlerAdvice {
    private static final String EXCEPTION_OCCURRED = "Exception occurred";
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);
    private static final String PARENT_SPAN_ID = "parentSpanId";
    private static final String TRACE_ID = "traceId";
    private static final String SPAN_ID = "spanId";
    private static final String X_B3_PARENT_SPAN_ID = "X-B3-ParentSpanId";
    private static final String X_B3_TRACE_ID = "X-B3-TraceId";
    private static final String X_B3_SPAN_ID = "X-B3-SpanId";
    private static final String COMPONENT_VALUE = "alliance-platform-maintenance-notifications";
    private static final String COMPONENT_KEY = "component";

    private final ResourceBundle resourceBundle = ResourceBundle.getBundle("exception-messages");
    private final String space;

    public ExceptionHandlerAdvice(@Value("${kamereon.suffix:}") String space) {
        this.space = space;
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<Error> handleAccessDeniedException(AccessDeniedException e) {
        return handleErrorCodeException(ErrorCodeException.buildErrorCodeException(ACCESS_DENIED));
    }

    @Override
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Error> handleBadRequestBodyException(MethodArgumentNotValidException e) {
        ErrorCodeException errorCodeException = buildErrorCodeException(MANDATORY_INPUT_MISSING,
                e.getBindingResult().getFieldError().getField(), String.valueOf(e.getBindingResult().getFieldError().getRejectedValue()));
        Error error = createError(errorCodeException.getErrorCode(), errorCodeException.getProperties());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UnresolvableVehicleIdentityException.class,
            VehicleIdentitiesEndpointException.class})
    public ResponseEntity<Error> handleVehicleIdentitiesDtoNotFound(Exception e) {
        ErrorCodeException exception = buildErrorCodeException(ClientErrorCode.VEHICLE_NOT_FOUND, "Unresolvable vehicle identity", e.getMessage());
        ErrorCode errorCode = exception.getErrorCode();
        Error error = createError(errorCode, exception.getProperties());
        return new ResponseEntity<>(error, errorCode.getStatus());
    }

    @ExceptionHandler({VehicleIdentitiesNotFoundException.class})
    public ResponseEntity<Error> handleVehicleIdentitiesDtoNotFound(VehicleIdentitiesNotFoundException e) {
        ErrorCodeException exception = buildErrorCodeException(ClientErrorCode.VEHICLE_NOT_FOUND, "vehicleId", e.getVehicleId());
        ErrorCode errorCode = exception.getErrorCode();
        Error error = createError(errorCode, exception.getProperties());
        return new ResponseEntity<>(error, errorCode.getStatus());
    }

    @ExceptionHandler({VinCryptEncryptException.class})
    public ResponseEntity<Error> handleVehicleIdentitiesDtoNotFound(VinCryptEncryptException e) {
        ErrorCodeException exception = buildErrorCodeException(ClientErrorCode.VEHICLE_NOT_FOUND, "can not encrypt", "vin");
        ErrorCode errorCode = exception.getErrorCode();
        Error error = createError(errorCode, exception.getProperties());
        return new ResponseEntity<>(error, errorCode.getStatus());
    }

    @ExceptionHandler({VinCryptDecryptException.class})
    public ResponseEntity<Error> handleVehicleIdentitiesDtoNotFound(VinCryptDecryptException e) {
        ErrorCodeException exception = buildErrorCodeException(ClientErrorCode.VEHICLE_NOT_FOUND, "can not decrypt", "vin");
        ErrorCode errorCode = exception.getErrorCode();
        Error error = createError(errorCode, exception.getProperties());
        return new ResponseEntity<>(error, errorCode.getStatus());
    }

    @Override
    public ResponseEntity<Error> handleConstraintViolationException(ConstraintViolationException e) {
        ErrorCodeException errorCodeException = buildErrorCodeException(MANDATORY_INPUT_MISSING,
                e.getConstraintViolations().iterator().next().getPropertyPath().toString(), "");
        Error error = createError(errorCodeException.getErrorCode(), errorCodeException.getProperties());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ErrorCodeException.class)
    public ResponseEntity<Error> handleErrorCodeException(ErrorCodeException e) {
        ErrorCode errorCode = e.getErrorCode();
        Error error = createError(errorCode, e.getProperties());

        return new ResponseEntity<>(error, errorCode.getStatus());
    }

    @Override
    @ExceptionHandler({Throwable.class})
    public ResponseEntity<Errors> handleThrowable(Throwable e) {
        ResponseEntity<Errors> errorsResponseEntity = super.handleThrowable(e);
        Errors errors = errorsResponseEntity.getBody();

        if (e instanceof ErrorCodeException) {
            ErrorCodeException errorCode = (ErrorCodeException) e;
            errors.setErrors(Collections.singletonList(createError(errorCode.getErrorCode(), errorCode.getProperties())));
            return new ResponseEntity<>(errors, errorCode.getErrorCode().getStatus());
        }

        formatErrors(errors);
        return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Error createError(ErrorCode errorCode, Map<String, String> properties) {
        Object[] parameters = properties
                .entrySet()
                .stream()
                .map(entry -> formatEntry(entry.getKey(), entry.getValue()))
                .toArray();

        Error error = new ErrorBuilder()
                .withTitle(resourceBundle.getString(errorCode.getTitle()))
                .withDetail(String.format(resourceBundle.getString(errorCode.getDetail()), parameters))
                .withCode(String.valueOf(errorCode.getCode()))
                .withStatus(errorCode.getStatus().getReasonPhrase())
                .build();

        error.setMeta(buildMetaMdcInfo());
        return error;
    }

    private String formatEntry(String key, String value) {
        return value.isEmpty() ? key : key + " - " + value;
    }

    private void formatErrors(Errors errors) {
        for (Error error : errors.getErrors()) {
            error.setTitle(resourceBundle.getString(SERVER_UNEXPECTED_ERROR.getTitle()));
            error.setCode(String.valueOf(SERVER_UNEXPECTED_ERROR.getCode()));
            error.setStatus(SERVER_UNEXPECTED_ERROR.getStatus().getReasonPhrase());
            error.setMeta(buildMetaMdcInfo());
        }
    }

    private ObjectNode buildMetaMdcInfo() {
        ObjectNode meta = JsonNodeFactory.instance.objectNode();
        meta.put(SPAN_ID, String.valueOf(MDC.get(X_B3_SPAN_ID)));
        meta.put(TRACE_ID, String.valueOf(MDC.get(X_B3_TRACE_ID)));
        meta.put(PARENT_SPAN_ID, String.valueOf(MDC.get(X_B3_PARENT_SPAN_ID)));
        meta.put(COMPONENT_KEY, getArtifactId());

        return meta;
    }

    private String getArtifactId() {
        return COMPONENT_VALUE + "-" + space.toLowerCase();
    }
}
