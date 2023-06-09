package ru.bmstu.labs.MainNode.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import ru.bmstu.labs.MainNode.dto.user.UserCreateRequest;
import ru.bmstu.labs.MainNode.dto.user.UserDeleteRequest;
import ru.bmstu.labs.MainNode.dto.user.UserGetRequest;
import ru.bmstu.labs.MainNode.dto.user.UserRequest;
import ru.bmstu.labs.MainNode.dto.user.UserUpdateRequest;
import ru.bmstu.labs.MainNode.issue.LabServiceException;
import ru.bmstu.labs.MainNode.model.User;
import ru.bmstu.labs.MainNode.service.NodeService;

@Component
@GraphQLApi
public class UserGraphQLApi implements CrudGraphQLApi<User, UserRequest, UserCreateRequest, UserGetRequest, UserUpdateRequest, UserDeleteRequest> {

	private final Logger log = LoggerFactory.getLogger(UserGraphQLApi.class);

	@Autowired
	private NodeService nodeService;

	public UserGraphQLApi(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	@Override
	@GraphQLQuery(name = "users")
	public List<User> getEntities(@GraphQLNonNull String alias) {
		return nodeService.getEntities(alias);
	}

	@Override
	@GraphQLQuery(name = "user")
	public User getEntity(UserGetRequest request) throws LabServiceException {
		return nodeService.getEntity(request);
	}

	@Override
	@GraphQLMutation(name = "createUser")
	public User createEntity(UserCreateRequest request) throws LabServiceException {
		return nodeService.createEntity(request);
	}

	@Override
	@GraphQLMutation(name = "updateUser")
	public User updateEntity(UserUpdateRequest request) throws LabServiceException {
		return nodeService.updateEntity(request);
	}

	@Override
	@GraphQLMutation(name = "deleteUser")
	public User deleteEntity(UserDeleteRequest request) throws LabServiceException {
		return nodeService.deleteEntity(request);
	}
}
