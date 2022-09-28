package com.acms.platform.maintenancenotifications.common.exception.errorcodes;


import com.acms.platform.maintenancenotifications.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public enum ServerErrorCode implements ErrorCode {

    SERVER_UNEXPECTED_ERROR(INTERNAL_SERVER_ERROR, 500000);

    private static final String ERROR_TYPE = "server";
    private static final String SEPARATION = ".";
    private static final String TITLE_TYPE = "title";
    private static final String DETAIL_TYPE = "detail";

    private final HttpStatus status;
    private final int code;
    private final String title;
    private final String detail;

    ServerErrorCode(HttpStatus status, int code) {
        this.status = status;
        this.code = code;
        this.title = formatMessage(TITLE_TYPE, code);
        this.detail = formatMessage(DETAIL_TYPE, code);
    }

    private String formatMessage(String type, int code) {
        return ERROR_TYPE + SEPARATION + code + SEPARATION + type;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDetail() {
        return detail;
    }

    @Override
    public String toString() {
        return "{" +
                "status=" + status +
                ", code=" + code +
                ", title='" + title + '\'' +
                ", detail='" + detail + '\'' +
                '}';
    }
}
