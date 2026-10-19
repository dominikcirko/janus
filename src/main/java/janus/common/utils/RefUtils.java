package janus.common.utils;

import janus.common.AbstractEntity;
import janus.common.exceptionhandler.exception.InvalidEntityReferenceException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public class RefUtils {

    /**
     * helper method for obtaining a hibernate proxy objects of entity by id when mapping domain objects to entities
     */
    public static <E extends AbstractEntity, R extends JpaRepository<E, UUID>> E entityRef(R repository, UUID entityId) {
        if (entityId == null) {
            throw new InvalidEntityReferenceException("Entity reference ID must not be null");
        }
        return repository.getReferenceById(entityId);
    }

}
