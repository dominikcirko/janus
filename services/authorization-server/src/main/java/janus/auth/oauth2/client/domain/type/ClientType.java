package janus.auth.oauth2.client.domain.type;

/**
 * OAuth client types based on their ability to keep credentials confidential.
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-2.1">RFC 6749, Section 2.1</a>
 */
public enum ClientType {
    PUBLIC, CONFIDENTIAL
}
