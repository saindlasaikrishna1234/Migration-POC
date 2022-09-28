package com.acms.platform.maintenancenotifications.common.exception;

import com.acms.platform.common.core.exception.handler.Error;
import com.acms.platform.common.core.exception.handler.Errors;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Component
public class ErrorHttpMessageConverter extends AbstractHttpMessageConverter<Error> {

    private ObjectMapper objectMapper;

    public ErrorHttpMessageConverter(ObjectMapper objectMapper) {
        super(MediaType.ALL);
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean supports(Class clazz) {
        return Error.class.equals(clazz);
    }

    @Override
    protected Error readInternal(Class clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void writeInternal(Error error, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        Errors errors = new Errors();
        errors.setErrors(Collections.singletonList(error));
        outputMessage.getBody().write(objectMapper.writeValueAsBytes(errors));
    }

}