package ru.bmstu.labs.MainNode.api;

import java.util.List;

import ru.bmstu.labs.MainNode.dto.LabDTO;
import ru.bmstu.labs.MainNode.issue.LabServiceException;
import ru.bmstu.labs.MainNode.model.IsolationEntity;

public interface CrudGraphQLApi<E extends IsolationEntity,
        T extends LabDTO,
        C extends T,
        R extends T,
        U extends T,
        D extends T> {

    List<E> getEntities(String alias) throws LabServiceException;

    E createEntity(C request) throws LabServiceException;

    E getEntity(R request) throws LabServiceException;

    E updateEntity(U request) throws LabServiceException;

    E deleteEntity(D request) throws LabServiceException;
}
