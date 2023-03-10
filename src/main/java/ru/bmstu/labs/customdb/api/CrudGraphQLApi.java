package ru.bmstu.labs.customdb.api;

import ru.bmstu.labs.customdb.dto.LabDTO;
import ru.bmstu.labs.customdb.issue.LabServiceException;
import ru.bmstu.labs.customdb.model.AbstractEntity;

import java.util.List;

public interface CrudGraphQLApi<E extends AbstractEntity,
        R extends LabDTO,
        C extends R,
        U extends R> {

    List<E> getEntities() throws LabServiceException;

    E getEntity(Long entityId) throws LabServiceException;

    E createEntity(C request) throws LabServiceException;

    E updateEntity(U request) throws LabServiceException;

    E deleteEntity(Long id) throws LabServiceException;
}
