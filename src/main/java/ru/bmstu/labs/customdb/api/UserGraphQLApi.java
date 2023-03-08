package ru.bmstu.labs.customdb.api;

import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.bmstu.labs.customdb.dto.TransactionRequest;
import ru.bmstu.labs.customdb.dto.user.UserCreateRequest;
import ru.bmstu.labs.customdb.dto.user.UserRequest;
import ru.bmstu.labs.customdb.dto.user.UserUpdateRequest;
import ru.bmstu.labs.customdb.issue.LabServiceException;
import ru.bmstu.labs.customdb.model.User;
import ru.bmstu.labs.customdb.service.UserService;

import java.util.List;

@Component
@GraphQLApi
public class UserGraphQLApi extends AbstractTransactionalGraphQLApi<User, UserRequest> {

    private final Logger log = LoggerFactory.getLogger(UserGraphQLApi.class);
    private UserService userService;

    public UserGraphQLApi(UserService userService) {
        this.userService = userService;
    }

    /*@Override
    @GraphQLQuery(name = "launch")
    public LaunchResponse launch() {
        return userService.launch();
    }

    @Override
    @GraphQLQuery(name = "terminate")
    public TerminateResponse terminate() {
        return userService.terminate();
    }*/

    @GraphQLQuery(name = "getUsers")
    public List<User> getEntities() throws LabServiceException {
        return userService.getEntities();
    }

    @GraphQLQuery(name = "getUser")
    public User getEntity(Long id) throws LabServiceException {
        return userService.getEntity(id);
    }

    @GraphQLMutation(name = "createUser")
    public User createEntity(UserCreateRequest request) throws LabServiceException {
        return userService.createEntity(request);
    }

    @GraphQLMutation(name = "updateUser")
    public User updateEntity(UserUpdateRequest request) throws LabServiceException {
        return userService.updateEntity(request);
    }

    @GraphQLMutation(name = "deleteUser")
    public User deleteEntity(Long id) throws LabServiceException {
        return userService.deleteEntity(id);
    }

    @Override
    @GraphQLMutation(name = "transaction")
    public List<User> transaction(List<TransactionRequest> requests) throws LabServiceException {
        userService.transaction(requests);
        return null;
    }
}
