package ru.bmstu.labs.customdb.api;

import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.bmstu.labs.customdb.dto.user.UserCreateRequest;
import ru.bmstu.labs.customdb.dto.user.UserRequest;
import ru.bmstu.labs.customdb.dto.user.UserUpdateRequest;
import ru.bmstu.labs.customdb.issue.LabServiceException;
import ru.bmstu.labs.customdb.model.User;
import ru.bmstu.labs.customdb.service.UserService;

import java.util.List;

@Component
@GraphQLApi
public class UserGraphQLApi implements CrudGraphQLApi<User, UserRequest, UserCreateRequest, UserUpdateRequest> {

    private final UserService userService;

    private final Logger log = LoggerFactory.getLogger(UserGraphQLApi.class);

    public UserGraphQLApi(UserService userService) {
        this.userService = userService;
    }

    @Override
    @GraphQLQuery(name = "users")
    public List<User> getEntities() {
        return userService.getEntities("");
    }

    @Override
    @GraphQLQuery(name = "user")
    public User getEntity(Long id) throws LabServiceException {
        return userService.getEntity("", id);
    }

    @GraphQLMutation(name = "createUser")
    public User createEntity(String alias, UserCreateRequest request) throws LabServiceException {
        return userService.createEntity(alias, request);
    }

    @GraphQLMutation(name = "updateUser")
    public User updateEntity(String alias, UserUpdateRequest request) throws LabServiceException {
        return userService.updateEntity(alias, request);
    }

    @GraphQLMutation(name = "deleteUser")
    public User deleteEntity(String alias, Long id) throws LabServiceException {
        return userService.deleteEntity(alias, id);
    }
}
