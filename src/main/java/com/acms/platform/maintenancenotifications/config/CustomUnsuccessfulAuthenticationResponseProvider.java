package com.acms.platform.maintenancenotifications.config;

import com.acms.platform.websecurity.UnsuccessfulAuthenticationResponseProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

import static com.acms.platform.maintenancenotifications.common.exception.errorcodes.ClientErrorCode.INVALID_TOKEN;

@Component
@Primary
public class CustomUnsuccessfulAuthenticationResponseProvider implements UnsuccessfulAuthenticationResponseProvider {

    @Value("${kamereon.suffix:}")
    private String space;

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomUnsuccessfulAuthenticationResponseProvider.class);
    private final ResourceBundle resourceBundle = ResourceBundle.getBundle("exception-messages");
    private static final String PARENT_SPAN_ID = "parentSpanId";
    private static final String TRACE_ID = "traceId";
    private static final String SPAN_ID = "spanId";
    private static final String X_B3_PARENT_SPAN_ID = "X-B3-ParentSpanId";
    private static final String X_B3_TRACE_ID = "X-B3-TraceId";
    private static final String X_B3_SPAN_ID = "X-B3-SpanId";
    private static final String COMPONENT_KEY = "component";
    private static final String COMPONENT_VALUE = "alliance-platform-maintenance-notifications";

    @Override
    public String provideResponse(AuthenticationException failed) {
        LOGGER.error("Authentication failed - {}", failed.getMessage());
        JSONObject json = new JSONObject();
        JSONObject metaJsonNode = new JSONObject();
        JSONArray array = new JSONArray();
        String response = null;
        try {
            metaJsonNode.put(SPAN_ID, String.valueOf(MDC.get(X_B3_SPAN_ID)));
            metaJsonNode.put(TRACE_ID, String.valueOf(MDC.get(X_B3_TRACE_ID)));
            metaJsonNode.put(PARENT_SPAN_ID, String.valueOf(MDC.get(X_B3_PARENT_SPAN_ID)));
            metaJsonNode.put(COMPONENT_KEY, getArtifactId());

            json.put("status", INVALID_TOKEN.getStatus().getReasonPhrase());
            json.put("code", String.valueOf(INVALID_TOKEN.getCode()));
            json.put("title", resourceBundle.getString(INVALID_TOKEN.getTitle()));
            json.put("detail", resourceBundle.getString(INVALID_TOKEN.getDetail()));
            json.put("meta", metaJsonNode);

            array.put(json);
            response = new JSONObject().put("errors", array).toString();
        } catch (JSONException e) {
            LOGGER.error("Error creating authentication exception response");
        }
        return response;
    }

    private String getArtifactId() {
        return COMPONENT_VALUE + "-" + space.toLowerCase();
    }
}
