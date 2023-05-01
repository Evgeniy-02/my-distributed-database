package ru.bmstu.labs.MainNode.service;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.bmstu.labs.MainNode.dto.user.UserCreateRequest;
import ru.bmstu.labs.MainNode.dto.user.UserDeleteRequest;
import ru.bmstu.labs.MainNode.dto.user.UserGetRequest;
import ru.bmstu.labs.MainNode.dto.user.UserUpdateRequest;
import ru.bmstu.labs.MainNode.issue.LabServiceException;
import ru.bmstu.labs.MainNode.model.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class NodeService {

    //    @Autowired
    private final DiscoveryClient discoveryClient;
    private final RestTemplate restTemplate;
    private final HttpHeaders headers;

    private List<ServiceInstance> nodes;
//    private static int counter = 0;

    public NodeService(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
        restTemplate = new RestTemplate();
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    /*private <R> User sendRequest(String urlPath, R request) throws LabServiceException {
        HttpEntity<R> httpRequest = new HttpEntity<>(request, headers);

        String url = getOriginalNodeUri() + urlPath;
        User user = restTemplate.postForObject(url, httpRequest, User.class);
        if (user == null)
            throw new LabServiceException("Received null in " + this + " from " + url);

        return user;
    }*/

    public List<User> getEntities(String alias) {
        updateNodeList();

        List<User> resultList = new ArrayList<>();

        for (ServiceInstance node : nodes) {
            HttpEntity<String> httpRequest = new HttpEntity<>(alias, headers);

            String url = node.getUri().toString() + "/users";
            User[] users = restTemplate.postForObject(url, httpRequest, User[].class);

            resultList.addAll(List.of(users != null ? users : new User[0]));
        }

        return resultList;

    }

    public User getEntity(UserGetRequest request) throws LabServiceException {
        HttpEntity<UserGetRequest> httpRequest = new HttpEntity<>(request, headers);

        // Main
        String originalUrl = getOriginalNodeUri(request.getId()) + "/user";
        User user = restTemplate.postForObject(originalUrl, httpRequest, User.class);
        if (user == null)
            throw new LabServiceException("Received null in " + this + " from " + originalUrl);

        // Replica
        String replicaUrl = getReplicaNodeUri(request.getId()) + "/user";
        if (null == restTemplate.postForObject(replicaUrl, httpRequest, User.class))
            throw new LabServiceException("Received null in " + this + " from " + originalUrl);

        return user;
    }

    public User createEntity(UserCreateRequest request) throws LabServiceException {
        HttpEntity<UserCreateRequest> httpRequest = new HttpEntity<>(request, headers);

        // Main
        String originalUrl = getOriginalNodeUri(request.getId()) + "/createUser";
        User user = restTemplate.postForObject(originalUrl, httpRequest, User.class);
        if (user == null)
            throw new LabServiceException("Received null in " + this + " from " + originalUrl);

        // Replica
        String replicaUrl = getReplicaNodeUri(request.getId()) + "/createUser";
        if (null == restTemplate.postForObject(replicaUrl, httpRequest, User.class))
            throw new LabServiceException("Received null in " + this + " from " + originalUrl);

        return user;
    }

    public User updateEntity(UserUpdateRequest request) throws LabServiceException {
        HttpEntity<UserUpdateRequest> httpRequest = new HttpEntity<>(request, headers);

        String url = getOriginalNodeUri(request.getId()) + "/updateUser";

        // Main
        User user = restTemplate.postForObject(url, httpRequest, User.class);
        if (user == null)
            throw new LabServiceException("Received null in " + this + " from " + url);

        // Replica
        String replicaUrl = getReplicaNodeUri(request.getId()) + "/createUser";
        if (null == restTemplate.postForObject(replicaUrl, httpRequest, User.class))
            throw new LabServiceException("Received null in " + this + " from " + url);

        return user;
    }

    public User deleteEntity(UserDeleteRequest request) throws LabServiceException {
        HttpEntity<UserDeleteRequest> httpRequest = new HttpEntity<>(request, headers);

        String url = getOriginalNodeUri(request.getId()) + "/updateUser";

        // Main
        User user = restTemplate.postForObject(url, httpRequest, User.class);
        if (user == null)
            throw new LabServiceException("Received null in " + this + " from " + url);

        // Replica
        String replicaUrl = getReplicaNodeUri(request.getId()) + "/createUser";
        if (null == restTemplate.postForObject(replicaUrl, httpRequest, User.class))
            throw new LabServiceException("Received null in " + this + " from " + url);

        return user;
    }

    private String getOriginalNodeUri(Long requestId) {
        updateNodeList();
        int nodeId = (int) (requestId % nodes.size());
        return nodes.get(nodeId).getUri().toString();
    }

    private String getReplicaNodeUri(Long requestId) {
        updateNodeList();
        int nodeId = (int) (++requestId % nodes.size());
        return nodes.get(nodeId).getUri().toString();
    }

    private void updateNodeList() {
        try {
            List<ServiceInstance> serviceInstances = discoveryClient.getInstances("node");

            if (serviceInstances != null && !serviceInstances.isEmpty()) {
                serviceInstances.sort(Comparator.comparingInt(ServiceInstance::getPort));
                nodes = serviceInstances;
//                if (counter >= nodes.size())
//                    counter = 0;
            } else {
                throw new LabServiceException("No one node-server is connected");
            }
        } catch (LabServiceException e) {
            e.printStackTrace();
        }
    }
}
