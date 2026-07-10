package io.github.janus.identity.domain.model;

import io.github.janus.identity.domain.type.CredentialType;
import lombok.*;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Credential {

    @Getter
    private UUID id;
    private CredentialType type;
    private String value;
    private UUID userId;

    public void hashPassword(CredentialType type, String hashedPassword) {
        if(type != CredentialType.PASSWORD || hashedPassword == null || hashedPassword.isEmpty()) {
            throw new RuntimeException("Not a password credential");
        }
        this.type = type;
        this.value = hashedPassword;
    }

}
