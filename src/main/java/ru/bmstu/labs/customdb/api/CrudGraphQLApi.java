package ru.bmstu.labs.customdb.api;

import ru.bmstu.labs.customdb.dto.LabDTO;
import ru.bmstu.labs.customdb.issue.LabServiceException;
import ru.bmstu.labs.customdb.model.IsolationEntity;

import java.util.List;

public interface CrudGraphQLApi<E extends IsolationEntity,
        R extends LabDTO,
        C extends R,
        U extends R> {

    List<E> getEntities(String alias) throws LabServiceException;

    E getEntity(String alias, Long entityId) throws LabServiceException;

    E createEntity(String alias, C request) throws LabServiceException;

    E updateEntity(String alias, U request) throws LabServiceException;

    E deleteEntity(String alias, Long id) throws LabServiceException;
}
