package ru.bmstu.labs.MainNode.api;

import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.stereotype.Component;
import ru.bmstu.labs.MainNode.dto.transaction.TransactionResponse;
import ru.bmstu.labs.MainNode.service.NodeService;

import java.util.List;

@Component
@GraphQLApi
public class TransactionalGraphQLApi {

    private final NodeService nodeService;

    public TransactionalGraphQLApi(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @GraphQLMutation(name = "begin")
    public List<TransactionResponse> beginTransaction(String alias) {
        return nodeService.beginTransaction(alias);
    }

    @GraphQLMutation(name = "commit")
    public List<TransactionResponse> commitTransaction(String alias) {
        return nodeService.commitTransaction(alias);
    }
}
