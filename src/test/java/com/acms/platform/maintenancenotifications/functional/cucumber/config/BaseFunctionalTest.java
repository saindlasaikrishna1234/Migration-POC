package com.acms.platform.maintenancenotifications.functional.cucumber.config;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.fail;

public abstract class BaseFunctionalTest {

    protected static final String KAUTH_TOKEN_INFO = "/info-token-uri?access_token=";
    protected static final String SYSTEM_ACCESS_TOKEN_WITH_SCOPES = "22222222-2222-2222-2222-222222222222";

    @Value(value = "classpath:test_data/token/payload_access_support.json")
    protected Resource token_with_support_scopes_content;

    public static String getResourceAsString(Resource input) {
        try {
            return IOUtils.toString(input.getInputStream(), UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Fail to read resource!!");
            return "";
        }
    }
}
