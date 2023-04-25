package ru.bmstu.labs.Node.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ru.bmstu.labs.Node.dto.user.UserCreateRequest;
import ru.bmstu.labs.Node.dto.user.UserDeleteRequest;
import ru.bmstu.labs.Node.dto.user.UserGetRequest;
import ru.bmstu.labs.Node.dto.user.UserUpdateRequest;
import ru.bmstu.labs.Node.issue.LabServiceException;
import ru.bmstu.labs.Node.model.User;
import ru.bmstu.labs.Node.service.UserService;

@RestController
public class UserGraphQLApi {

	private final UserService userService;

	private final Logger log = LoggerFactory.getLogger(UserGraphQLApi.class);

	public UserGraphQLApi(UserService userService) {
		this.userService = userService;
	}

	@RequestMapping(value = "/users", method = RequestMethod.POST)
	public List<User> getEntities(@RequestBody() String alias) {
		return userService.getEntities(alias);
	}

	@RequestMapping(value = "/user", method = RequestMethod.POST)
	public User getEntity(@RequestBody UserGetRequest request) throws LabServiceException {
		return userService.getEntity(request);
	}

	@RequestMapping(value = "/createUser", method = RequestMethod.POST)
	public User createEntity(@RequestBody UserCreateRequest request) throws LabServiceException {
		return userService.createEntity(request);
	}

	@RequestMapping(value = "/updateUser", method = RequestMethod.POST)
	public User updateEntity(@RequestBody UserUpdateRequest request) throws LabServiceException {
		return userService.updateEntity(request);
	}

	@RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
	public User deleteEntity(@RequestBody UserDeleteRequest request) throws LabServiceException {
		return userService.deleteEntity(request);
	}
}
