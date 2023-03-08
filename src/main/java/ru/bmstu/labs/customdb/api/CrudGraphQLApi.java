package ru.bmstu.labs.customdb.api;

import ru.bmstu.labs.customdb.dto.LabDTO;
import ru.bmstu.labs.customdb.model.AbstractEntity;

import java.util.List;

public interface CrudGraphQLApi<E extends AbstractEntity,
        R extends LabDTO,
        C extends R,
        U extends R> {

    public List<E> getEntities();

    public E getEntity();

    public E createEntity(C request);

    public E updateEntity(U request);

    public E deleteEntity(Long entityId);
}
