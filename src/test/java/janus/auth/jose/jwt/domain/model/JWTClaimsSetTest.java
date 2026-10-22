package janus.auth.jose.jwt.domain.model;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class JWTClaimsSetTest {

    public static final String TEST_ISSUER = "test_issuer";
    public static final String TEST_SUBJECT = "test_subject";
    public static final String TEST_AUDIENCE = "test_audience";
    public static final Duration TEST_TOKEN_LIFETIME = Duration.ofMinutes(5);

    @Test
    void testWhenContextValid_thenCreateExpectedClaims() {
        var context = new TokenIssuanceContext(
                TEST_ISSUER,
                TEST_SUBJECT,
                TEST_AUDIENCE,
                TEST_TOKEN_LIFETIME,
                Map.of()
        );
        var beforeCreation = Instant.now().getEpochSecond();

        var claimsSet = new JWTClaimsSet(context);

        var afterCreation = Instant.now().getEpochSecond();
        assertEquals(TEST_ISSUER, claimsSet.getIss());
        assertEquals(TEST_SUBJECT, claimsSet.getSub());
        assertEquals(TEST_AUDIENCE, claimsSet.getAud());
        assertTrue(claimsSet.getIat() >= beforeCreation);
        assertTrue(claimsSet.getIat() <= afterCreation);
        assertEquals(claimsSet.getIat(), claimsSet.getNbf());
        assertEquals(
                claimsSet.getIat() + TEST_TOKEN_LIFETIME.toSeconds(),
                claimsSet.getExp()
        );
        assertDoesNotThrow(() -> UUID.fromString(claimsSet.getJti()));
    }

    @Test
    void testWhenCustomClaimsProvided_thenSerializeThemAtTopLevel() {
        var objectMapper = new ObjectMapper();
        var context = new TokenIssuanceContext(
                TEST_ISSUER,
                TEST_SUBJECT,
                TEST_AUDIENCE,
                TEST_TOKEN_LIFETIME,
                Map.of("department", "marketing")
        );
        var claimsSet = new JWTClaimsSet(context);

        var json = objectMapper.readTree(objectMapper.writeValueAsBytes(claimsSet));

        assertEquals("marketing", json.get("department").asString());
        assertFalse(json.has("customClaims"));
    }
}
