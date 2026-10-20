package janus.auth.jose.jwt.domain.model;

import janus.auth.jose.jwt.domain.exception.InvalidTokenContextException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TokenIssuanceContextTest {

    public static final String TEST_ISSUER = "test_issuer";
    public static final String TEST_SUBJECT = "test_subject";
    public static final String TEST_AUDIENCE = "test_audience";
    public static final Duration TEST_DURATION_NEGATIVE = Duration.ofMinutes(1).negated();
    public static final Duration TEST_DURATION = Duration.ofMinutes(1);
    public static final Map<String, Object> CUSTOM_CLAIMS_WITHOUT_REGISTERED= Map.of("department", "marketing");
    public static final Map<String, Object> CUSTOM_CLAIMS_WITH_REGISTERED= Map.of("iss", "test_issuer");

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void testWhenIssuerInvalidThenThrow(String issuer){
        var exception = assertThrows(
                InvalidTokenContextException.class,
                () -> new TokenIssuanceContext(
                        issuer, TEST_SUBJECT, TEST_AUDIENCE, TEST_DURATION,
                        CUSTOM_CLAIMS_WITHOUT_REGISTERED)
        );
        assertEquals("issuer must not be blank", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void testWhenSubjectInvalidThenThrow(String subject){
        var exception = assertThrows(
                InvalidTokenContextException.class,
                () -> new TokenIssuanceContext(
                        TEST_ISSUER, subject, TEST_AUDIENCE, TEST_DURATION,
                        CUSTOM_CLAIMS_WITHOUT_REGISTERED)
        );
        assertEquals("subject must not be blank", exception.getMessage());

    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void testWhenAudienceInvalidThenThrow(String audience){
        var exception = assertThrows(
                InvalidTokenContextException.class,
                () -> new TokenIssuanceContext(
                        TEST_ISSUER, TEST_SUBJECT, audience, TEST_DURATION,
                        CUSTOM_CLAIMS_WITHOUT_REGISTERED)
        );
        assertEquals("audience must not be blank", exception.getMessage());
    }

    @Test
    void testWhenTokenLifetimeNullThenThrow(){
        var exception = assertThrows(
                InvalidTokenContextException.class,
                () -> new TokenIssuanceContext(
                TEST_ISSUER, TEST_SUBJECT, TEST_AUDIENCE, null,
                CUSTOM_CLAIMS_WITHOUT_REGISTERED)
        );
        assertEquals("tokenLifetime must not be null", exception.getMessage());

    }

    @Test
    void testWhenTokenLifetimeLessThanZeroThenThrow(){
        var exception = assertThrows(
                InvalidTokenContextException.class,
                () -> new TokenIssuanceContext(
                        TEST_ISSUER, TEST_SUBJECT, TEST_AUDIENCE, TEST_DURATION_NEGATIVE,
                        CUSTOM_CLAIMS_WITHOUT_REGISTERED)
        );
        assertEquals("tokenLifetime must be at least one second", exception.getMessage());
    }

    @Test
    void whenCustomClaimsNullThenUseEmptyMap(){
        var ctx = new TokenIssuanceContext(
                TEST_ISSUER, TEST_SUBJECT, TEST_AUDIENCE, TEST_DURATION, null);
        assertEquals(ctx.customClaims(), Map.of());
    }

    @Test
    void whenCustomClaimsHasRegisteredClaimsThenThrow(){
        var exception = assertThrows(
                InvalidTokenContextException.class,
                () -> new TokenIssuanceContext(
                TEST_ISSUER, TEST_SUBJECT, TEST_AUDIENCE, TEST_DURATION,
                CUSTOM_CLAIMS_WITH_REGISTERED));
        assertEquals("customClaims must not contain registered claim: iss", exception.getMessage());
    }


}
