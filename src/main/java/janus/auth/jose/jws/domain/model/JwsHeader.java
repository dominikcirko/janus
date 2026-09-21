package janus.auth.jose.jws.domain.model;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import janus.auth.jose.common.Algorithm;
import janus.auth.jose.common.JoseCommonParams;

//https://datatracker.ietf.org/doc/html/rfc7515#page-9
//https://datatracker.ietf.org/doc/html/rfc7519#section-5.1 - for typ in JWT
public record JwsHeader(Algorithm alg, @JsonUnwrapped JoseCommonParams params) { }

