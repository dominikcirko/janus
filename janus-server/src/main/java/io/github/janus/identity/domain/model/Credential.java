package io.github.janus.identity.domain.model;

import io.github.janus.identity.domain.type.CredentialType;
import lombok.*;

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

    public void setPasswordHash(CredentialType type, String password) {
        if(password == null || password.isEmpty()) {
            throw new RuntimeException("Password hash cannot be empty");
        }
        this.type = type;
        this.value = password;
    }

}
