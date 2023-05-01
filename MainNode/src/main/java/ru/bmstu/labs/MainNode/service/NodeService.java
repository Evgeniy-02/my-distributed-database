package ru.bmstu.labs.MainNode.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.bmstu.labs.MainNode.dto.user.*;
import ru.bmstu.labs.MainNode.issue.LabServiceException;
import ru.bmstu.labs.MainNode.model.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class NodeService {

    private static final String GET_ENTITY_PATH = "/user";
    public static final String CREATE_ENTITY_PATH = "/createUser";
    public static final String UPDATE_ENTITY_PATH = "/updateUser";
    public static final String DELETE_ENTITY_PATH = "/deleteUser";
    public static final String GET_ENTITIES_PATH = "/users";
    public static final String SYNC_PATH = "/sync";

    private final DiscoveryClient discoveryClient;
    private final RestTemplate restTemplate;
    private final HttpHeaders headers;

    private final Logger log = LoggerFactory.getLogger(NodeService.class);

    private List<ServiceInstance> nodes;

    public NodeService(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
        restTemplate = new RestTemplate();
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    public List<User> getEntities(String alias) {
        log.debug("method=getEntities message='Get users request received: alias={}'", alias);

        updateNodeList();

        List<User> resultList = new ArrayList<>();
        List<User> users = new ArrayList<>();

        for (ServiceInstance node : nodes) {
            HttpEntity<String> getEntitiesRequest = new HttpEntity<>(alias, headers);

            String url = node.getUri().toString() + GET_ENTITIES_PATH;
            ResponseEntity<List<User>> nodeUsersResponse = restTemplate.exchange(url, HttpMethod.POST, getEntitiesRequest, new ParameterizedTypeReference<List<User>>() {
            });
            List<User> nodeUsers = nodeUsersResponse.getBody();

            log.debug("method=getEntities message='User for node {} requested: {}'", node.getUri(), nodeUsers);
            if (nodeUsers != null) {
                users.addAll(nodeUsers);
            }
        }

        users.sort(Comparator.comparing(User::getId));

        for (int i = 0; i < users.size() - 1; i++) {
            if (Objects.equals(users.get(i).getId(), users.get(i + 1).getId())) {
                User user = getLastVersion(users.get(i), users.get(i + 1));

                syncEntity(user);

                i++;
                if (user != null && user.getDeletedAt() == null) {
                    resultList.add(user);
                }
            }
        }

        log.debug("method=getEntities message='Users requested successfully: {}'", resultList);
        return resultList;
    }

    public User getEntity(UserGetRequest request) {
        log.debug("method=getEntity message='Get user request received: request={}'", request);
        HttpEntity<UserGetRequest> httpRequest = new HttpEntity<>(request, headers);

        // Main
        String originalUrl = getOriginalNodeUri(request.getId()) + GET_ENTITY_PATH;
        User originalUser = restTemplate.postForObject(originalUrl, httpRequest, User.class);
        log.debug("method=getEntity message='Original user requested successfully: {}'", originalUser);

        // Replica
        String replicaUrl = getReplicaNodeUri(request.getId()) + GET_ENTITY_PATH;
        User replicaUser = restTemplate.postForObject(replicaUrl, httpRequest, User.class);
        log.debug("method=getEntity message='Replica user requested successfully: {}'", replicaUser);

        User user = getLastVersion(originalUser, replicaUser);

        syncEntity(user);

        log.debug("method=getEntity message='User requested successfully: {}'", user);
        if (user != null && user.getDeletedAt() != null) {
            return null;
        }
        return user;
    }

    public User createEntity(UserCreateRequest request) {
        log.debug("method=createEntity message='Create user request received: request={}'", request);
        HttpEntity<UserCreateRequest> httpRequest = new HttpEntity<>(request, headers);

        // Main
        String originalUrl = getOriginalNodeUri(request.getId()) + CREATE_ENTITY_PATH;
        User originalUser = restTemplate.postForObject(originalUrl, httpRequest, User.class);
        log.debug("method=createEntity message='Original user created successfully: {}'", originalUser);

        // Replica
        String replicaUrl = getReplicaNodeUri(request.getId()) + CREATE_ENTITY_PATH;
        User replicaUser = restTemplate.postForObject(replicaUrl, httpRequest, User.class);
        log.debug("method=createEntity message='Replica user created successfully: {}'", replicaUser);

        log.debug("method=createEntity message='User created successfully: {}'", originalUser);
        return originalUser;
    }

    public User updateEntity(UserUpdateRequest request) {
        log.debug("method=updateEntity message='Update user request received: request={}'", request);
        HttpEntity<UserUpdateRequest> httpRequest = new HttpEntity<>(request, headers);

        // Main
        String url = getOriginalNodeUri(request.getId()) + UPDATE_ENTITY_PATH;
        User originalUser = restTemplate.postForObject(url, httpRequest, User.class);
        log.debug("method=updateEntity message='Original user updated successfully: {}'", originalUser);

        // Replica
        String replicaUrl = getReplicaNodeUri(request.getId()) + UPDATE_ENTITY_PATH;
        User replicaUser = restTemplate.postForObject(replicaUrl, httpRequest, User.class);
        log.debug("method=updateEntity message='Replica user updated successfully: {}'", replicaUser);

        log.debug("method=updateEntity message='User updated successfully: {}'", originalUser);
        return originalUser;
    }

    public User deleteEntity(UserDeleteRequest request) {
        log.debug("method=deleteEntity message='Delete user request received: request={}'", request);
        HttpEntity<UserDeleteRequest> httpRequest = new HttpEntity<>(request, headers);

        // Main
        String url = getOriginalNodeUri(request.getId()) + DELETE_ENTITY_PATH;
        User originalUser = restTemplate.postForObject(url, httpRequest, User.class);
        log.debug("method=deleteEntity message='Original user deleted successfully: {}'", originalUser);

        // Replica
        String replicaUrl = getReplicaNodeUri(request.getId()) + DELETE_ENTITY_PATH;
        User replicaUser = restTemplate.postForObject(replicaUrl, httpRequest, User.class);
        log.debug("method=deleteEntity message='Replica user deleted successfully: {}'", replicaUser);

        log.debug("method=deleteEntity message='User deleted successfully: {}'", originalUser);
        return originalUser;
    }

    private String getOriginalNodeUri(Long requestId) {
        log.debug("method=getOriginalNodeUri message='Original node uri requested: requestId={}'", requestId);

        updateNodeList();
        int nodeId = (int) (requestId % nodes.size());
        String originalNodeUri = nodes.get(nodeId).getUri().toString();

        log.debug("method=getOriginalNodeUri message='Original node uri received successfully: {}'", originalNodeUri);
        return originalNodeUri;
    }

    private String getReplicaNodeUri(Long requestId) {
        log.debug("method=getReplicaNodeUri message='Replica node uri requested: requestId={}'", requestId);

        updateNodeList();
        int nodeId = (int) (++requestId % nodes.size());
        String replicaNodeUri = nodes.get(nodeId).getUri().toString();

        log.debug("method=getReplicaNodeUri message='Replica node uri received successfully: {}'", replicaNodeUri);
        return replicaNodeUri;
    }

    private void updateNodeList() {
        try {
            List<ServiceInstance> serviceInstances = discoveryClient.getInstances("node");

            if (serviceInstances != null && !serviceInstances.isEmpty()) {
                serviceInstances.sort(Comparator.comparingInt(ServiceInstance::getPort));
                nodes = serviceInstances;
            } else {
                throw new LabServiceException("No one node-server is connected");
            }
        } catch (LabServiceException e) {
            e.printStackTrace();
        }
    }

    private User getLastVersion(User firstUser, User secondUser) {
        log.debug("method=getLastVersion message='Last version requested between: firstUser={}, secondUser={}'",
                firstUser, secondUser);

        if (firstUser == null && secondUser == null) {
            return null;
        }

        if (firstUser == null) {
            return secondUser;
        }
        if (secondUser == null) {
            return firstUser;
        }

        // check deleted at field
        if (!Objects.equals(firstUser.getDeletedAt(), secondUser.getDeletedAt())) {
            if (firstUser.getDeletedAt() == null) {
                return secondUser;
            }
            if (secondUser.getDeletedAt() == null) {
                return firstUser;
            }

            return firstUser.getDeletedAt() > secondUser.getDeletedAt() ? firstUser : secondUser;
        }

        // check updated at field
        if (!Objects.equals(firstUser.getUpdatedAt(), secondUser.getUpdatedAt())) {
            if (firstUser.getUpdatedAt() == null) {
                return secondUser;
            }
            if (secondUser.getUpdatedAt() == null) {
                return firstUser;
            }

            return firstUser.getUpdatedAt() > secondUser.getUpdatedAt() ? firstUser : secondUser;
        }

        // check created at field
        if (!Objects.equals(firstUser.getCreatedAt(), secondUser.getCreatedAt())) {
            if (firstUser.getCreatedAt() == null) {
                return secondUser;
            }
            if (secondUser.getCreatedAt() == null) {
                return firstUser;
            }

            return firstUser.getCreatedAt() > secondUser.getCreatedAt() ? firstUser : secondUser;
        }

        return firstUser;
    }

    private void syncEntity(User user) {
        log.debug("method=syncEntity message='Synchronization for user requested: {}'", user);

        if (user != null) {
            UserRequest userRequest = new UserRequest();

            userRequest.setId(user.getId());
            userRequest.setName(user.getName());
            userRequest.setLastname(user.getLastName());
            userRequest.setEmail(user.getEmail());
            userRequest.setCreatedAt(user.getCreatedAt());
            userRequest.setUpdatedAt(user.getUpdatedAt());
            userRequest.setDeletedAt(user.getDeletedAt());

            HttpEntity<UserRequest> syncRequest = new HttpEntity<>(userRequest, headers);

            restTemplate.postForObject(getOriginalNodeUri(user.getId()) + SYNC_PATH, syncRequest, User.class);
            restTemplate.postForObject(getReplicaNodeUri(user.getId()) + SYNC_PATH, syncRequest, User.class);
        }
    }
}
