package janus.identity.user.adapter.out.persistence.entity;

import jakarta.persistence.*;
import janus.common.AbstractEntity;
import janus.identity.user.domain.type.UserStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@Table(name = "user")
public class UserEntity extends AbstractEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private UserStatus status;

    @Column(name = "email", nullable = false, length = 320)
    private String email;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "given_name")
    private String givenName;

    @Column(name = "family_name")
    private String familyName;

    @Column(name = "picture_uri", length = 2048)
    private String pictureUri;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "user_metadata", nullable = false, columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> userMetadata = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "system_metadata", nullable = false, columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> systemMetadata = new HashMap<>();

}
