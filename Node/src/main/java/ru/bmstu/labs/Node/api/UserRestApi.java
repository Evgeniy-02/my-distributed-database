package ru.bmstu.labs.Node.api;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ru.bmstu.labs.Node.dto.transaction.TransactionResponse;
import ru.bmstu.labs.Node.dto.user.*;
import ru.bmstu.labs.Node.issue.LabServiceException;
import ru.bmstu.labs.Node.model.User;
import ru.bmstu.labs.Node.service.DatabaseService;
import ru.bmstu.labs.Node.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserRestApi {

    private final UserService userService;
    private final DatabaseService databaseService;

    private final Logger log = LoggerFactory.getLogger(UserRestApi.class);

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public List<User> getEntities(@RequestBody() String alias) {
        return userService.getEntities(alias);
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public User getEntity(@RequestBody UserGetRequest request) {
        try {
            return userService.getEntity(request);
        } catch (LabServiceException e) {
            log.warn("method=getEntity message='{}'", e.getMessage());
            return null;
        }
    }

    @RequestMapping(value = "/createUser", method = RequestMethod.POST)
    public User createEntity(@RequestBody UserCreateRequest request) {
        try {
            return userService.createEntity(request);
        } catch (LabServiceException e) {
            log.warn("method=createEntity message='{}'", e.getMessage());
            return null;
        }
    }

    @RequestMapping(value = "/updateUser", method = RequestMethod.POST)
    public User updateEntity(@RequestBody UserUpdateRequest request) {
        try {
            return userService.updateEntity(request);
        } catch (LabServiceException e) {
            log.warn("method=updateEntity message='{}'", e.getMessage());
            return null;
        }
    }

    @RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
    public User deleteEntity(@RequestBody UserDeleteRequest request) {
        try {
            return userService.deleteEntity(request);
        } catch (LabServiceException e) {
            log.warn("method=deleteEntity message='{}'", e.getMessage());
            return null;
        }
    }

    @RequestMapping(value = "/sync", method = RequestMethod.POST)
    public User syncEntity(@RequestBody UserRequest request) {
        try {
            return userService.syncEntity(request);
        } catch (LabServiceException e) {
            log.warn("method=syncEntity message='{}'", e.getMessage());
            return null;
        }
    }

    @RequestMapping(value = "/begin", method = RequestMethod.POST)
    public TransactionResponse beginTransaction(@RequestBody String alias) {
        return databaseService.beginTransaction(alias);
    }

    @RequestMapping(value = "/commit", method = RequestMethod.POST)
    public TransactionResponse commitTransaction(@RequestBody String alias) {
        try {
            return databaseService.commitTransaction(alias);
        } catch (LabServiceException e) {
            log.warn("method=commitTransaction message='{}'", e.getMessage());
            return null;
        }
    }
}
