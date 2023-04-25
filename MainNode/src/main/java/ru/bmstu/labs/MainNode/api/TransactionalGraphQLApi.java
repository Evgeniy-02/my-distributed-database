package ru.bmstu.labs.MainNode.api;

import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import ru.bmstu.labs.MainNode.dto.transaction.TransactionResponse;
import ru.bmstu.labs.MainNode.issue.LabServiceException;

import org.springframework.stereotype.Component;

@Component
@GraphQLApi
public class TransactionalGraphQLApi {

	@GraphQLMutation(name = "begin")
	public TransactionResponse beginTransaction(String alias) {
		// TODO implementation
		return new TransactionResponse("Method is not implemented");
	}

	@GraphQLMutation(name = "commit")
	public TransactionResponse commitTransaction(String alias) throws LabServiceException {
		// TODO implementation
		return new TransactionResponse("Method is not implemented");
	}
}
