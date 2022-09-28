package com.acms.platform.maintenancenotifications.common.exception;

import org.springframework.http.HttpStatus;

import java.io.Serializable;

public interface ErrorCode extends Serializable {
    HttpStatus getStatus();

    int getCode();

    String getTitle();

    String getDetail();
}
