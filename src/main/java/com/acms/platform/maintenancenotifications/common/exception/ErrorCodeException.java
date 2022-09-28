package com.acms.platform.maintenancenotifications.common.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringJoiner;

import static org.apache.commons.collections4.MapUtils.emptyIfNull;

public class ErrorCodeException extends RuntimeException {

    private final ResourceBundle resourceBundle = ResourceBundle.getBundle("exception-messages");

    private static final String DELIMITER = " ; ";
    private static final String HYPHEN = " - ";
    private static final String COLON = ": ";

    private final ErrorCode errorCode;
    private final Map<String, String> properties;

    public ErrorCodeException(Context context) {
        super(buildName(context.errorCode, context.properties));
        this.errorCode = context.errorCode;
        this.properties = context.properties;
    }

    public ErrorCodeException(Throwable cause, Context context) {
        super(buildName(context.errorCode, context.properties), cause);
        this.errorCode = context.errorCode;
        this.properties = context.properties;
    }

    public static ErrorCodeException buildErrorCodeException(Throwable exception, Context context) {
        if (exception instanceof ErrorCodeException) {
            ErrorCodeException kuaEx = (ErrorCodeException) exception;
            if (context != null && !context.errorCode.getClass().isAssignableFrom(kuaEx.getErrorCode().getClass())) {
                return new ErrorCodeException(exception, context);
            }
            return kuaEx;
        }
        return new ErrorCodeException(exception, context);
    }

    public static ErrorCodeException buildErrorCodeException(Throwable exception) {
        return buildErrorCodeException(exception, null);
    }

    public static ErrorCodeException buildErrorCodeException(ErrorCode error) {
        return new ErrorCodeException(ContextBuilder.init(error).get());
    }

    public static ErrorCodeException buildErrorCodeException(ErrorCode error, String propertyName, String propertyValue) {
        return new ErrorCodeException(ContextBuilder.init(error).add(propertyName, propertyValue).get());
    }

    public static ErrorCodeException buildErrorCodeException(ErrorCode error, Map<String, String> properties) {
        return new ErrorCodeException(ContextBuilder.init(error).add(properties).get());
    }

    private static String buildName(ErrorCode errorCode, Map<String, String> property) {
        StringJoiner joiner = new StringJoiner(DELIMITER);
        property.entrySet().stream().forEach(s -> joiner.add(s.getKey() + HYPHEN + s.getValue()));
        return errorCode.getDetail() + COLON + joiner;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public String getErrorMessage() {
        Object[] properties = this.getProperties().entrySet().stream().map(entry -> formatEntry(entry.getKey(), entry.getValue())).toArray();
        return String.format(resourceBundle.getString(this.getErrorCode().getDetail()), properties);
    }

    public String getLogErrorMessage() {
        return "ERROR " + this.getErrorCode().getCode() + " : " + this.getErrorMessage() +".";
    }

    private String formatEntry(String key, String value) {
        return key + " - " + value;
    }

    private static class Context {
        private final ErrorCode errorCode;
        private final Map<String, String> properties;

        private Context(ErrorCode errorCode, Map<String, String> properties) {
            this.errorCode = errorCode;
            this.properties = properties;
        }
    }

    private static class ContextBuilder {
        private final ErrorCode errorCode;
        private final Map<String, String> properties = new LinkedHashMap<>();

        private ContextBuilder(ErrorCode errorCode) {
            this.errorCode = errorCode;
        }

        public static ContextBuilder init(ErrorCode errorCode) {
            return new ContextBuilder(errorCode);
        }

        public ContextBuilder add(String key, String value) {
            properties.put(key, value);
            return this;
        }

        public ContextBuilder add(Map<String, String> properties) {
            emptyIfNull(properties).forEach(this.properties::put);
            return this;
        }

        public Context get() {
            return new Context(errorCode, properties);
        }
    }
}
