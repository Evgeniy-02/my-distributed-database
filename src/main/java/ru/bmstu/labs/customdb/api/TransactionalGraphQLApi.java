package ru.bmstu.labs.customdb.api;

import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.stereotype.Component;
import ru.bmstu.labs.customdb.dto.transaction.TransactionResponse;
import ru.bmstu.labs.customdb.issue.LabServiceException;
import ru.bmstu.labs.customdb.service.DatabaseService;

@Component
@GraphQLApi
public class TransactionalGraphQLApi {

    private DatabaseService databaseService;

    public TransactionalGraphQLApi(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @GraphQLMutation(name = "begin")
    public TransactionResponse beginTransaction() {
        return databaseService.beginTransaction();
    }

    @GraphQLMutation(name = "commit")
    public TransactionResponse commitTransaction() throws LabServiceException {
        return databaseService.commitTransaction();
    }

    @GraphQLMutation(name = "rollback")
    public TransactionResponse rollbackTransaction() throws LabServiceException {
        return databaseService.rollbackTransaction();
    }
}
