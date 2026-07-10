package io.github.janus.identity.domain.model;

import io.github.janus.identity.domain.type.CredentialType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Credential {

    private UUID id;
    private CredentialType type;
    private String value;

    public void setPassword(CredentialType type, String hashedPassword) {
        if(type != CredentialType.PASSWORD || value == null || value.isEmpty()) {
            throw new RuntimeException("Not a password credential");
        }
        this.value = hashedPassword;
    }

}
