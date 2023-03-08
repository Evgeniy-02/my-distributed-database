package ru.bmstu.labs.customdb.api;

import ru.bmstu.labs.customdb.dto.LabDTO;
import ru.bmstu.labs.customdb.dto.LaunchResponse;
import ru.bmstu.labs.customdb.dto.TerminateResponse;
import ru.bmstu.labs.customdb.dto.TransactionRequest;
import ru.bmstu.labs.customdb.issue.LabServiceException;
import ru.bmstu.labs.customdb.model.AbstractEntity;

import java.util.List;

public class AbstractTransactionalGraphQLApi<E extends AbstractEntity, R extends LabDTO>
        implements TransactionalGraphQLApi<E, R> {

    @Override
    public LaunchResponse launch() {
        return null;
    }

    @Override
    public TerminateResponse terminate() {
        return null;
    }

    @Override
    public List<E> transaction(List<TransactionRequest> requests) throws LabServiceException {
        return null;
    }
}
