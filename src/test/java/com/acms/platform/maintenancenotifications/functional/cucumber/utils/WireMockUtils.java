package com.acms.platform.maintenancenotifications.functional.cucumber.utils;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;


import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.http.HttpHeaders.CONNECTION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;

public class WireMockUtils {

    public static String getResourceAsString(Resource input) {
        try {
            return IOUtils.toString(input.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            TestCase.fail("");
            return "";
        }
    }

    public static void stubGetUrl(String urlPath, String responseBody, HttpStatus httpStatus) {
        stubGetUrl(responseBody, httpStatus, urlEqualTo(urlPath));
    }

    public static void stubGetUrl(String urlPath, String responseBody) {
        stubGetUrl(responseBody, OK, urlEqualTo(urlPath));
    }

    public static void stubGetUrlReturnsForbidden(String urlPath, String responseBody) {
        stubGetUrl(responseBody, FORBIDDEN, urlEqualTo(urlPath));
    }

    public static void stubMatchingGetUrl(String urlPath, String responseBody) {
        stubGetUrl(responseBody, OK, urlMatching(urlPath));
    }

    public static void stubMatchingGetUrlFail(String urlPath, HttpStatus status) {
        stubGetUrl(null, status, urlMatching(urlPath));
    }

    public static void stubPutUrl(String urlPath) {
        stubPutUrl(urlEqualTo(urlPath), null, OK);
    }

    public static void stubPutUrl(String urlPath, String responseBody) {
        stubPutUrl(urlEqualTo(urlPath), responseBody, OK);
    }

    public static void stubMatchingPutUrl(String urlPath) {
        stubPutUrl(urlMatching(urlPath), null, OK);
    }

    public static void stubPostUrl(String urlPath, String responseBody) {
        stubPostUrl(urlPath, responseBody, OK);
    }

    public static void stubPostUrl(String urlPath, String responseBody, HttpStatus httpStatus) {
        stubPostUrl(urlEqualTo(urlPath), responseBody, httpStatus);
    }

    public static void stubMatchingPostUrl(String urlPath) {
        stubPostUrl(urlMatching(urlPath), null, OK);
    }

    public static void stubPatchUrl(String urlPath) {
        stubPatchUrl(urlEqualTo(urlPath), null, OK);
    }

    public static void stubPatchUrl(String urlPath, HttpStatus status) {
        stubPatchUrl(urlEqualTo(urlPath), null, status);
    }

    public static void stubMatchingPatchUrl(String urlPath) {
        stubPatchUrl(urlMatching(urlPath), null, OK);
    }

    public static void stubDeleteUrl(String urlPath) {
        stubDeleteUrl(urlEqualTo(urlPath), OK, null);
    }

    public static void stubDeleteUrl(String urlPath, HttpStatus status) {
        stubDeleteUrl(urlEqualTo(urlPath), status, null);
    }

    public static void stubDeleteUrl(String urlPath, HttpStatus status, String responseBody) {
        stubDeleteUrl(urlEqualTo(urlPath), status, responseBody);
    }

    public static void stubMatchingDeleteUrl(String urlPath) {
        stubDeleteUrl(urlMatching(urlPath), OK, null);
    }

    public static void stubMatchingDeleteUrl(String urlPath, HttpStatus status) {
        stubDeleteUrl(urlMatching(urlPath), status, null);
    }

    private static void stubDeleteUrl(UrlPattern urlPattern, HttpStatus status, String responseBody) {
        stubFor(delete(urlPattern)
                .willReturn(getMockResponse(responseBody, status)));
    }

    private static void stubGetUrl(String responseBody, HttpStatus httpStatus, UrlPattern urlPattern) {
        stubFor(get(urlPattern)
                .willReturn(getMockResponse(responseBody, httpStatus)));
    }

    private static void stubPostUrl(UrlPattern urlPattern, String responseBody, HttpStatus httpStatus) {
        stubFor(post(urlPattern)
                .willReturn(getMockResponse(responseBody, httpStatus)));
    }

    private static void stubPutUrl(UrlPattern urlPattern, String responseBody, HttpStatus httpStatus) {
        stubFor(put(urlPattern)
                .willReturn(getMockResponse(responseBody, httpStatus)));
    }

    public static void stubPatchUrl(UrlPattern urlPattern, String responseBody, HttpStatus httpStatus) {
        stubFor(patch(urlPattern)
                .willReturn(getMockResponse(responseBody, httpStatus)));
    }

    private static ResponseDefinitionBuilder getMockResponse(String responseBody, HttpStatus httpStatus) {
        return aResponse()
                .withStatus(httpStatus.value())
                .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .withHeader(CONNECTION, "Close")
                .withBody(responseBody);
    }
}
