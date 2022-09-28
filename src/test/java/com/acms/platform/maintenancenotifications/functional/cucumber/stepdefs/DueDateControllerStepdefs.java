package com.acms.platform.maintenancenotifications.functional.cucumber.stepdefs;

import com.acms.platform.maintenancenotifications.functional.cucumber.config.BaseFunctionalTest;
import com.acms.platform.maintenancenotifications.repository.NotificationVehicleRepository;
import com.acms.platform.maintenancenotifications.repository.VehicleParamRepository;
import com.acms.platform.maintenancenotifications.repository.entities.NotificationVehicleEntity;
import com.acms.platform.maintenancenotifications.repository.entities.VehicleParamEntity;
import com.aic.framework.vehicleidentity.VehicleIdentityType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.io.IOUtils;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.acms.platform.maintenancenotifications.functional.cucumber.utils.WireMockUtils.stubGetUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DueDateControllerStepdefs extends BaseFunctionalTest {

    private static final String KDIS_GET_VEHICLE_IDENTITY_URL = "/v1/vehicles/{vehicleId}/identity?discoveryState=DISCOVERED";
    private static final String ENCRYPTED_VIN = "wBEcfyuyDiqqiaGSr5jdutf2g2skZZQO8kogEBCwtkI=";

    private static final String GET_VEHICLE_SERVICES_URL = "/vehicle-services/v1/vehicle/{vehicleId}/services";
    private static final String DUE_DATE_URL = "/v1/users/{userId}/vehicles/{vehicleId}/duedate/";
    private static final LocalDate _01_01_2000 = LocalDate.of(2000, 1, 1);
    private static final LocalDate _01_01_2020 = LocalDate.of(2020, 1, 1);
    public static final String SPAN_ID = "${SPANID}";
    public static final String TRACE_ID = "${TRACEID}";

    private final MockMvc mockMvc;
    private final VehicleParamRepository vehicleParamRepository;
    private final NotificationVehicleRepository notificationVehicleRepository;
    private final ObjectMapper objectMapper;

    public DueDateControllerStepdefs(MockMvc mockMvc,
                                     VehicleParamRepository vehicleParamRepository,
                                     NotificationVehicleRepository notificationVehicleRepository,
                                     ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.vehicleParamRepository = vehicleParamRepository;
        this.notificationVehicleRepository = notificationVehicleRepository;
        this.objectMapper = objectMapper;
    }

    @Value("classpath:test_data/due_date/post/post_due_date_valid_request.json")
    private Resource postDueDate;
    @Value("classpath:test_data/due_date/post/post_due_date_without_date_request.json")
    private Resource postDueDateWithoutDateParameterRequest;
    @Value("classpath:test_data/due_date/get/get_due_date_success_response.json")
    private Resource getDueDateResponseBody;
    @Value("classpath:test_data/due_date/get/no_due_date_stored_for_vehicle_response.json")
    private Resource noDueDateStoredForVehicleResponse;
    @Value("classpath:test_data/due_date/get/access_denied_error_response.json")
    private Resource accessDeniedErrorResponse;
    @Value("classpath:test_data/due_date/get/authentication_error_response.json")
    private Resource authenticationErrorResponse;
    @Value("classpath:test_data/due_date/post/no_due_date_parameter_post_response.json")
    private Resource noDueDateParameterPostResponse;
    @Value(value = "classpath:test_data/token/get_vehicle_services_response_body.json")
    private Resource getVehicleServicesResponseBody;
    @Value(value = "classpath:test_data/token/get_vehicle_services_empty_response_body.json")
    private Resource getVehicleServicesEmptyResponseBody;
    @Value(value = "classpath:test_data/vehicle/get_vehicle_identity.json")
    private Resource getVehicleIdentityResponse;

    private MvcResult mvcResult;
    private String token;
    private String vehicleUuid;
    private String userId;

    @Given("User id =\"([^\"]*)\"$")
    public void given_user_id(String userId) {
        this.userId = userId;
    }

    @Given("Vehicle Identification Number with vehicleUuid =\"([^\"]*)\"$")
    public void vehicle_Identification_Number_with_vehicleUuid(String vehicleUuid) {
        this.vehicleUuid = vehicleUuid;
        stubGetUrl(KDIS_GET_VEHICLE_IDENTITY_URL.replace("{vehicleId}", vehicleUuid), getResourceAsString(getVehicleIdentityResponse));
    }

    @Given("Vehicle Identification Number with vehicleUuid =\"([^\"]*)\" stub with encrypted vin$")
    public void vehicle_Identification_Number_with_vehicleUuid_stubbed_with_encrypted_vin(String vehicleUuid) {
        this.vehicleUuid = vehicleUuid;
        stubGetUrl(KDIS_GET_VEHICLE_IDENTITY_URL.replace("{vehicleId}", ENCRYPTED_VIN), getResourceAsString(getVehicleIdentityResponse));
    }

    @Given("System token with token =\"([^\"]*)\"$")
    public void system_token_with_token(String token) {
        this.token = token;
    }

    @Given("a vehicle where the maintenance due date was never set")
    public void due_date_never_set() {
        //
    }

    @Given("a vehicle was sent some maintenance alert previously")
    public void given_histories_are_saved_for_the_vehicle() {
        notificationVehicleRepository.save(new NotificationVehicleEntity().setVehicleUuid(vehicleUuid));
        notificationVehicleRepository.save(new NotificationVehicleEntity().setVehicleUuid(vehicleUuid));
    }

    @Given("a vehicle where the maintenance due date was already set")
    public void due_date_already_set() {
        vehicleParamRepository.save(new VehicleParamEntity().setDueDate(_01_01_2000).setVehicleUuid("e9adf97b-ba7d-4671-b56f-9fd5e56151ae"));
    }

    @Given("Vehicle services returns service 101")
    public void vehicle_services_returns_service_101() {
        stubGetUrl(GET_VEHICLE_SERVICES_URL.replace("{vehicleId}", vehicleUuid), getResourceAsString(getVehicleServicesResponseBody));
    }

    @Given("Vehicle services returns no service")
    public void vehicle_services_returns_no_service() {
        stubGetUrl(GET_VEHICLE_SERVICES_URL.replace("{vehicleId}", vehicleUuid), getResourceAsString(getVehicleServicesEmptyResponseBody));
    }

    @When("the end-point is called")
    public void called() {
        try {
            mvcResult = mockMvc
                    .perform(post(DUE_DATE_URL.replace("{userId}", userId).replace("{vehicleId}", vehicleUuid))
                            .header(AUTHORIZATION, forgeAccessToken(token))
                            .header("X-VehicleIdType", VehicleIdentityType.UUID)
                            .header(CONTENT_TYPE, MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(IOUtils.toByteArray(postDueDate.getInputStream()))
                    )
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
            fail("post due date fail");
        }
    }

    @When("the end-point is called without the date")
    public void whenPostDueDateIsCalledWithoutDueDate() {
        try {
            mvcResult = mockMvc
                    .perform(post(DUE_DATE_URL.replace("{userId}", userId).replace("{vehicleId}", vehicleUuid))
                            .header(AUTHORIZATION, forgeAccessToken(token))
                            .header("X-VehicleIdType", VehicleIdentityType.UUID)
                            .header(CONTENT_TYPE, MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(IOUtils.toByteArray(postDueDateWithoutDateParameterRequest.getInputStream()))
                    )
                    .andExpect(status().isBadRequest())
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
            fail("post due date fail");
        }
    }

    @When("the get due date is called")
    public void get_called() {
        try {
            mvcResult = mockMvc
                    .perform(get(DUE_DATE_URL.replace("{userId}", userId).replace("{vehicleId}", vehicleUuid))
                            .header(AUTHORIZATION, forgeAccessToken(token))
                            .header("X-VehicleIdType", VehicleIdentityType.UUID)
                            .header(CONTENT_TYPE, MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                    )
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
            fail("post due date fail");
        }
    }

    @When("the get due date is called with vin =\"([^\"]*)\"$")
    public void get_called_with_vin(String vin) {
        try {
            mvcResult = mockMvc
                    .perform(get(DUE_DATE_URL.replace("{userId}", userId).replace("{vehicleId}", vin))
                            .header(AUTHORIZATION, forgeAccessToken(token))
                            .header("X-VehicleIdType", VehicleIdentityType.VIN)
                            .header(CONTENT_TYPE, MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
            fail("post due date fail");
        }
    }

    @Then("the maintenance due date is set for this vehicle")
    public void create_and_set_due_date() {
        try {

            assertThat(HttpStatus.OK.value()).isEqualTo(mvcResult.getResponse().getStatus());
            assertThat(vehicleParamRepository.findByVehicleUuid(vehicleUuid)).isNotNull();
        } catch (Exception e) {

            e.printStackTrace();
            fail("return_maintenance_due_date failed");
        }
    }

    @Then("the previous maintenance alert histories are erased")
    public void then_no_history_for_this_vehicle() {
        assertThat(notificationVehicleRepository.findAll()).isEmpty();
    }

    @Then("the maintenance due date is modified for this vehicle")
    public void update_due_date() {
        try {

            assertThat(HttpStatus.OK.value()).isEqualTo(mvcResult.getResponse().getStatus());
            VehicleParamEntity updatedVehicleParam = vehicleParamRepository.findByVehicleUuid(vehicleUuid);
            assertThat(_01_01_2020).isEqualTo(updatedVehicleParam.getDueDate());
            assertThat(updatedVehicleParam.getModificationDate()).isNotNull();
        } catch (Exception e) {

            e.printStackTrace();
            fail("return_maintenance_due_date failed");
        }
    }

    @Then("the latest due date is returned")
    public void retrieve_due_date() {
        try {
            verify(getRequestedFor(urlEqualTo(GET_VEHICLE_SERVICES_URL.replace("{vehicleId}", vehicleUuid))));
            assertEquals(200, mvcResult.getResponse().getStatus());
            JSONAssert.assertEquals(mvcResult.getResponse().getContentAsString(), IOUtils.toString(getDueDateResponseBody.getInputStream()), JSONCompareMode.LENIENT);
        } catch (Exception e) {

            e.printStackTrace();
            fail("return_maintenance_due_date failed");
        }
    }

    @Then("a 404002 error is returned")
    public void retrieve_due_date_error() {
        try {
            String expectedResponse = getResourceAsString(noDueDateStoredForVehicleResponse);
            assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus());
            JsonNode jsonNode = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
            Map<String, String> params = new HashMap<>();
            String spanId = jsonNode.get("errors").get(0).get("meta").get("spanId").textValue();
            String traceId = jsonNode.get("errors").get(0).get("meta").get("traceId").textValue();

            assertThat(traceId).isNotEqualTo("null");
            assertThat(spanId).isNotEqualTo("null");
            JSONAssert.assertEquals(expectedResponse.replace(SPAN_ID, spanId).replace(TRACE_ID, traceId),
                    mvcResult.getResponse().getContentAsString(), JSONCompareMode.LENIENT);
        } catch (Exception e) {

            e.printStackTrace();
            fail("return_maintenance_due_date failed");
        }
    }

    @Then("a 403000 error is returned")
    public void get_due_date_access_denied_error() {
        try {
            String expectedResponse = getResourceAsString(accessDeniedErrorResponse);
            assertEquals(HttpStatus.FORBIDDEN.value(), mvcResult.getResponse().getStatus());
            JsonNode jsonNode = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
            Map<String, String> params = new HashMap<>();
            String spanId = jsonNode.get("errors").get(0).get("meta").get("spanId").textValue();
            String traceId = jsonNode.get("errors").get(0).get("meta").get("traceId").textValue();

            assertThat(traceId).isNotEqualTo("null");
            assertThat(spanId).isNotEqualTo("null");
            JSONAssert.assertEquals(expectedResponse.replace(SPAN_ID, spanId).replace(TRACE_ID, traceId),
                    mvcResult.getResponse().getContentAsString(), JSONCompareMode.LENIENT);
        } catch (Exception e) {

            e.printStackTrace();
            fail("return_maintenance_due_date failed");
        }
    }

    @Then("a 401000 error is returned")
    public void get_due_date_unauthorized_error() {
        try {
            String expectedResponse = getResourceAsString(authenticationErrorResponse);
            assertEquals(HttpStatus.UNAUTHORIZED.value(), mvcResult.getResponse().getStatus());
            JsonNode jsonNode = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
            Map<String, String> params = new HashMap<>();
            String spanId = jsonNode.get("errors").get(0).get("meta").get("spanId").textValue();
            String traceId = jsonNode.get("errors").get(0).get("meta").get("traceId").textValue();

            assertThat(traceId).isNotEqualTo("null");
            assertThat(spanId).isNotEqualTo("null");
            JSONAssert.assertEquals(expectedResponse.replace(SPAN_ID, spanId).replace(TRACE_ID, traceId),
                    mvcResult.getResponse().getContentAsString(), JSONCompareMode.LENIENT);
        } catch (Exception e) {

            e.printStackTrace();
            fail("return_maintenance_due_date failed");
        }
    }

    @Then("the 400002 error is returned formatted")
    public void post_due_date_body_validation_error() {
        try {
            String expectedResponse = getResourceAsString(noDueDateParameterPostResponse);
            assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
            JsonNode jsonNode = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
            Map<String, String> params = new HashMap<>();
            String spanId = jsonNode.get("errors").get(0).get("meta").get("spanId").textValue();
            String traceId = jsonNode.get("errors").get(0).get("meta").get("traceId").textValue();

            assertThat(traceId).isNotEqualTo("null");
            assertThat(spanId).isNotEqualTo("null");
            JSONAssert.assertEquals(expectedResponse.replace(SPAN_ID, spanId).replace(TRACE_ID, traceId),
                    mvcResult.getResponse().getContentAsString(), JSONCompareMode.LENIENT);
        } catch (Exception e) {

            e.printStackTrace();
            fail("return_maintenance_due_date failed");
        }
    }

    @Then("check vehicle identity is called")
    public void checkVehicleIdentityIsCalled() {
        verify(getRequestedFor(urlEqualTo(KDIS_GET_VEHICLE_IDENTITY_URL.replace("{vehicleId}", vehicleUuid))));
    }

    @Then("check vehicle identity is called with encrypted vin")
    public void checkVehicleIdentityIsCalledWithEncryptedVin() {
        verify(getRequestedFor(urlEqualTo(KDIS_GET_VEHICLE_IDENTITY_URL.replace("{vehicleId}", ENCRYPTED_VIN))));
    }

    private String forgeAccessToken(String token) {
        return "Bearer " + token;
    }

}

