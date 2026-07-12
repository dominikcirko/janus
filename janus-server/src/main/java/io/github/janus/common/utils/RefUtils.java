package io.github.janus.common.utils;

import io.github.janus.common.AbstractEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public class RefUtils {

    /**
     * helper method for fetching entities by id when mapping domain objects to entities
     */
    public static <E extends AbstractEntity, R extends JpaRepository<E, UUID>> E entityRef(R repository, UUID entityId) {
        if(entityId == null) { throw new IllegalArgumentException("entityId is null"); }
        return repository.getReferenceById(entityId);
    }

}
