package ru.bmstu.labs.MainNode.api;

import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.stereotype.Component;
import ru.bmstu.labs.MainNode.dto.user.*;
import ru.bmstu.labs.MainNode.model.User;
import ru.bmstu.labs.MainNode.service.NodeService;

import java.util.List;

@Component
@GraphQLApi
public class UserGraphQLApi implements CrudGraphQLApi<User, UserRequest, UserCreateRequest, UserGetRequest, UserUpdateRequest, UserDeleteRequest> {

    private final NodeService nodeService;

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
    public User getEntity(UserGetRequest request) {
        return nodeService.getEntity(request);
    }

    @Override
    @GraphQLMutation(name = "createUser")
    public User createEntity(UserCreateRequest request) {
        return nodeService.createEntity(request);
    }

    @Override
    @GraphQLMutation(name = "updateUser")
    public User updateEntity(UserUpdateRequest request) {
        return nodeService.updateEntity(request);
    }

    @Override
    @GraphQLMutation(name = "deleteUser")
    public User deleteEntity(UserDeleteRequest request) {
        return nodeService.deleteEntity(request);
    }
}
