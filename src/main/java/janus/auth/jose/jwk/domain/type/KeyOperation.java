package janus.auth.jose.jwk.domain.type;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;


/**
 * todo: check for duplicates - RFC 7517: "Duplicate key operation values MUST NOT be
 *    present in the array"

 *         var listValues = Arrays.asList(KeyOperations.values());
 *         var duplicates = listValues.stream().filter(
 *                 v -> Collections.frequency(listValues, value) > 1).toList();
 *
 *         if(!duplicates.isEmpty()) {
 *             throw new IllegalArgumentException("Duplicate key operation values must not be present");
 *         }
 * */

//https://datatracker.ietf.org/doc/html/rfc7517#page-4
//https://www.w3.org/TR/webcrypto/#subtlecrypto-interface-methods
@RequiredArgsConstructor
public enum KeyOperation {

    SIGN("sign"),
    VERIFY("verify"),
    ENCRYPT("encrypt"),
    DECRYPT("decrypt"),
    WRAP_KEY("wrapKey"),
    UNWRAP_KEY("unwrapKey"),
    DERIVE_KEY("deriveKey"),
    DERIVE_BITS("deriveBits");

    private final String value;

    @JsonValue
    public String getValue(){
        return this.value;
    }

}
