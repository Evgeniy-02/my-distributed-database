package ru.bmstu.labs.MainNode.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ru.bmstu.labs.MainNode.issue.LabServiceException;
import ru.bmstu.labs.MainNode.dto.user.UserCreateRequest;
import ru.bmstu.labs.MainNode.dto.user.UserDeleteRequest;
import ru.bmstu.labs.MainNode.dto.user.UserGetRequest;
import ru.bmstu.labs.MainNode.dto.user.UserUpdateRequest;
import ru.bmstu.labs.MainNode.model.User;

@Service
public class NodeService {

	@Autowired
	private DiscoveryClient discoveryClient;

	private List<ServiceInstance> nodes;
	private static int counter = 0;

	final RestTemplate restTemplate;
	final HttpHeaders headers;

	public NodeService() {
		restTemplate = new RestTemplate();
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
	}

	private String currentNodeUrl(boolean updateNodeList) {
		if (updateNodeList)
			updateNodeList();
		return ((ServiceInstance) nodes.get(counter)).getUri().toString();
	}

	private String nextNodeUrl() {
		updateNodeList();
		counter = ++counter % nodes.size();
		return currentNodeUrl(false);
	}

	private <R> User sendRequest(String urlPath, R request) throws LabServiceException {
		HttpEntity httpRequest = new HttpEntity<R>(request, headers);

		String url = currentNodeUrl(true) + urlPath;
		User user = restTemplate.postForObject(url, httpRequest, User.class);
		if (user == null)
			throw new LabServiceException("Received null in " + this + " from " + url);

		return user;
	}

	public List<User> getEntities(String alias) {
		updateNodeList();

		List<User> resultList = new ArrayList<>();

		for (int i = 0; i < nodes.size(); i++) {
			HttpEntity httpRequest = new HttpEntity<String>(alias, headers);

			String url = ((ServiceInstance) nodes.get(i)).getUri().toString() + "/users";
			User[] users = restTemplate.postForObject(url, httpRequest, User[].class);

			resultList.addAll(List.of(users));
		}

		return resultList;

	}

	// TODO for all connected nodes
	public User getEntity(UserGetRequest request) throws LabServiceException {
		return sendRequest("/user", request);
	}

	public User createEntity(UserCreateRequest request) throws LabServiceException {
		HttpEntity httpRequest = new HttpEntity<UserCreateRequest>(request, headers);

		String url = currentNodeUrl(true) + "/createUser";

		// Main insert
		User user = restTemplate.postForObject(url, httpRequest, User.class);
		if (user == null)
			throw new LabServiceException("Received null in " + this + " from " + url);

		// Replica
		String replicaUrl = nextNodeUrl() + "/createUser";
		if (null == restTemplate.postForObject(replicaUrl, httpRequest, User.class))
			throw new LabServiceException("Received null in " + this + " from " + url);

		return user;
	}

	// TODO for all connected nodes
	public User updateEntity(UserUpdateRequest request) throws LabServiceException {
		return sendRequest("/updateUser", request);
	}

	// TODO for all connected nodes
	public User deleteEntity(UserDeleteRequest request) throws LabServiceException {
		return sendRequest("/deleteUser", request);
	}

	private void updateNodeList() {
		try {
			List<ServiceInstance> serviceInstances = discoveryClient.getInstances("node");

			if (serviceInstances != null && !serviceInstances.isEmpty()) {
				serviceInstances.sort((arg0, arg1) -> Integer.compare(arg0.getPort(), arg1.getPort()));
				nodes = serviceInstances;
				if (counter >= nodes.size())
					counter = 0;
			} else {
				throw new LabServiceException("No one node-server is connected");
			}
		} catch (LabServiceException e) {
			e.printStackTrace();
		}
	}
}
