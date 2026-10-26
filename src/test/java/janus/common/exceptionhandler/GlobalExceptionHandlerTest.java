package janus.common.exceptionhandler;

import janus.common.exceptionhandler.testhelper.GlobalExceptionHandlerTestController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static janus.common.exceptionhandler.testhelper.GlobalExceptionHandlerTestController.JANUS_EXCEPTION_ENDPOINT;
import static janus.common.exceptionhandler.testhelper.GlobalExceptionHandlerTestController.NULL_POINTER_EXCEPTION_ENDPOINT;
import static janus.common.exceptionhandler.testhelper.GlobalExceptionHandlerTestController.REQUEST_VALIDATION_ENDPOINT;
import static janus.common.exceptionhandler.testhelper.GlobalExceptionHandlerTestController.TYPE_MISMATCH_ENDPOINT;
import static janus.common.exceptionhandler.testhelper.GlobalExceptionHandlerTestController.UNEXPECTED_EXCEPTION_ENDPOINT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GlobalExceptionHandlerTestController.class)
@Import(GlobalExceptionHandler.class)
public class GlobalExceptionHandlerTest {

    private static final String SUCCESS_PATH = "$.success";
    private static final String ERROR_CODE_PATH = "$.error.errorCode";
    private static final String ERROR_MESSAGE_PATH = "$.error.message";
    private static final String ERROR_STATUS_CODE_PATH = "$.error.statusCode";
    private static final String FIRST_ERROR_SUGGESTION_PATH = "$.error.suggestions[0]";
    private static final String FIRST_ERROR_DETAIL_PATH = "$.error.details[0]";
    private static final String DEBUG_MESSAGE_PATH = "$.error.debugMessage";
    private static final String REQUEST_PATH = "$.path";

    private static final String TEST_CONFLICT_ERROR_CODE = "TEST_CONFLICT";
    private static final String TEST_CONFLICT_MESSAGE = "Test conflict";
    private static final String TEST_CONFLICT_SUGGESTION = "Resolve the conflict";

    private static final String BLANK_NAME_REQUEST = "{\"name\":\"\"}";
    private static final String VALIDATION_ERROR_CODE = "VALIDATION_ERROR";
    private static final String VALIDATION_ERROR_MESSAGE = "Invalid request parameters";
    private static final String BLANK_NAME_ERROR_DETAIL = "name: must not be blank";

    private static final String MALFORMED_JSON_REQUEST = "{";
    private static final String MALFORMED_REQUEST_ERROR_CODE = "MALFORMED_REQUEST";
    private static final String MALFORMED_REQUEST_ERROR_MESSAGE = "Malformed JSON request";

    private static final String TYPE_MISMATCH_PARAMETER = "value";
    private static final String INVALID_INTEGER_VALUE = "not-a-number";
    private static final String TYPE_MISMATCH_ERROR_CODE = "TYPE_MISMATCH";
    private static final String TYPE_MISMATCH_ERROR_MESSAGE =
            "Parameter 'value' should be of type 'Integer'";

    private static final String NULL_POINTER_ERROR_CODE = "NULL_POINTER_ERROR";
    private static final String NULL_POINTER_ERROR_MESSAGE =
            "An unexpected error occurred while processing your request";

    private static final String INTERNAL_SERVER_ERROR_CODE = "INTERNAL_SERVER_ERROR";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE =
            "An unexpected error occurred. Our team has been notified.";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testWhenJanusExceptionThrown_thenReturnExceptionDetails() throws Exception {
        mockMvc.perform(get(JANUS_EXCEPTION_ENDPOINT))
                .andExpect(status().isConflict())
                .andExpect(jsonPath(SUCCESS_PATH).value(false))
                .andExpect(jsonPath(ERROR_CODE_PATH).value(TEST_CONFLICT_ERROR_CODE))
                .andExpect(jsonPath(ERROR_MESSAGE_PATH).value(TEST_CONFLICT_MESSAGE))
                .andExpect(jsonPath(ERROR_STATUS_CODE_PATH).value(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath(FIRST_ERROR_SUGGESTION_PATH).value(TEST_CONFLICT_SUGGESTION))
                .andExpect(jsonPath(REQUEST_PATH).value(JANUS_EXCEPTION_ENDPOINT));
    }

    @Test
    void testWhenRequestValidationFails_thenReturnValidationError() throws Exception {
        mockMvc.perform(post(REQUEST_VALIDATION_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BLANK_NAME_REQUEST))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_CODE_PATH).value(VALIDATION_ERROR_CODE))
                .andExpect(jsonPath(ERROR_MESSAGE_PATH).value(VALIDATION_ERROR_MESSAGE))
                .andExpect(jsonPath(FIRST_ERROR_DETAIL_PATH).value(BLANK_NAME_ERROR_DETAIL));
    }

    @Test
    void testWhenRequestBodyMalformed_thenReturnMalformedRequestError() throws Exception {
        mockMvc.perform(post(REQUEST_VALIDATION_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MALFORMED_JSON_REQUEST))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_CODE_PATH).value(MALFORMED_REQUEST_ERROR_CODE))
                .andExpect(jsonPath(ERROR_MESSAGE_PATH).value(MALFORMED_REQUEST_ERROR_MESSAGE));
    }

    @Test
    void testWhenArgumentTypeMismatched_thenReturnTypeMismatchError() throws Exception {
        mockMvc.perform(get(TYPE_MISMATCH_ENDPOINT)
                        .param(TYPE_MISMATCH_PARAMETER, INVALID_INTEGER_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_CODE_PATH).value(TYPE_MISMATCH_ERROR_CODE))
                .andExpect(jsonPath(ERROR_MESSAGE_PATH).value(TYPE_MISMATCH_ERROR_MESSAGE));
    }

    @Test
    void testWhenNullPointerExceptionThrown_thenReturnSanitizedError() throws Exception {
        mockMvc.perform(get(NULL_POINTER_EXCEPTION_ENDPOINT))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath(ERROR_CODE_PATH).value(NULL_POINTER_ERROR_CODE))
                .andExpect(jsonPath(ERROR_MESSAGE_PATH).value(NULL_POINTER_ERROR_MESSAGE));
    }

    @Test
    void testWhenUnexpectedExceptionThrown_thenReturnInternalServerError() throws Exception {
        mockMvc.perform(get(UNEXPECTED_EXCEPTION_ENDPOINT))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath(ERROR_CODE_PATH).value(INTERNAL_SERVER_ERROR_CODE))
                .andExpect(jsonPath(ERROR_MESSAGE_PATH).value(INTERNAL_SERVER_ERROR_MESSAGE))
                .andExpect(jsonPath(DEBUG_MESSAGE_PATH).doesNotExist());
    }

}
