package janus.auth.jose.jwt.domain.model;

import janus.auth.jose.jwt.domain.exception.InvalidTokenContextException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TokenIssuanceContextTest {

    public static final String TEST_ISSUER = "test_issuer";
    public static final String TEST_SUBJECT = "test_subject";
    public static final String TEST_AUDIENCE = "test_audience";
    public static final Duration TEST_TOKEN_LIFETIME_NEGATED = Duration.ofMinutes(1).negated();
    public static final Duration TEST_TOKEN_LIFETIME = Duration.ofMinutes(1);
    public static final Map<String, Object> CUSTOM_CLAIMS_WITHOUT_REGISTERED= Map.of("department", "marketing");
    public static final Map<String, Object> CUSTOM_CLAIMS_WITH_REGISTERED= Map.of("iss", "test_issuer");

    @Test
    void testWhenAllInputsValid_thenCreateTokenIssuanceContext(){
        var ctx = new TokenIssuanceContext(
                TEST_ISSUER, TEST_SUBJECT, TEST_AUDIENCE, TEST_TOKEN_LIFETIME,
                CUSTOM_CLAIMS_WITHOUT_REGISTERED);
        assertNotNull(ctx);
        assertEquals(TEST_ISSUER, ctx.issuer());
        assertEquals(TEST_SUBJECT, ctx.subject());
        assertEquals(TEST_AUDIENCE, ctx.audience());
        assertEquals(TEST_TOKEN_LIFETIME, ctx.tokenLifetime());
        assertEquals(CUSTOM_CLAIMS_WITHOUT_REGISTERED, ctx.customClaims());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void testWhenIssuerInvalid_thenThrow(String issuer){
        var exception = assertThrows(
                InvalidTokenContextException.class,
                () -> new TokenIssuanceContext(
                        issuer, TEST_SUBJECT, TEST_AUDIENCE, TEST_TOKEN_LIFETIME,
                        CUSTOM_CLAIMS_WITHOUT_REGISTERED)
        );
        assertEquals("issuer must not be blank", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void testWhenSubjectInvalid_thenThrow(String subject){
        var exception = assertThrows(
                InvalidTokenContextException.class,
                () -> new TokenIssuanceContext(
                        TEST_ISSUER, subject, TEST_AUDIENCE, TEST_TOKEN_LIFETIME,
                        CUSTOM_CLAIMS_WITHOUT_REGISTERED)
        );
        assertEquals("subject must not be blank", exception.getMessage());

    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void testWhenAudienceInvalid_thenThrow(String audience){
        var exception = assertThrows(
                InvalidTokenContextException.class,
                () -> new TokenIssuanceContext(
                        TEST_ISSUER, TEST_SUBJECT, audience, TEST_TOKEN_LIFETIME,
                        CUSTOM_CLAIMS_WITHOUT_REGISTERED)
        );
        assertEquals("audience must not be blank", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("invalidLifetimes")
    void whenLifetimeInvalid_thenThrow(Duration lifetime, String expectedMessage) {
        var exception = assertThrows(
                InvalidTokenContextException.class,
                () -> new TokenIssuanceContext(
                        TEST_ISSUER, TEST_SUBJECT, TEST_AUDIENCE,
                        lifetime, null)
        );
        assertEquals(expectedMessage, exception.getMessage());
    }

    private static Stream<Arguments> invalidLifetimes(){
        return Stream.of(
                Arguments.of(null, "tokenLifetime must not be null"),
                Arguments.of(Duration.ZERO, "tokenLifetime must be at least one second"),
                Arguments.of(TEST_TOKEN_LIFETIME_NEGATED, "tokenLifetime must be at least one second")
        );
    }

    @Test
    void whenCustomClaimsNull_thenUseEmptyMap(){
        var ctx = new TokenIssuanceContext(
                TEST_ISSUER, TEST_SUBJECT, TEST_AUDIENCE, TEST_TOKEN_LIFETIME, null);
        assertEquals(ctx.customClaims(), Map.of());
    }

    @Test
    void whenCustomClaimsHasRegisteredClaims_thenThrow(){
        var exception = assertThrows(
                InvalidTokenContextException.class,
                () -> new TokenIssuanceContext(
                TEST_ISSUER, TEST_SUBJECT, TEST_AUDIENCE, TEST_TOKEN_LIFETIME,
                CUSTOM_CLAIMS_WITH_REGISTERED));
        assertEquals("customClaims must not contain registered claim: iss", exception.getMessage());
    }


}
