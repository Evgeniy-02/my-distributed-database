package ru.bmstu.labs.customdb.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.bmstu.labs.customdb.dto.TransactionRequest;
import ru.bmstu.labs.customdb.dto.user.UserCreateRequest;
import ru.bmstu.labs.customdb.dto.user.UserUpdateRequest;
import ru.bmstu.labs.customdb.issue.LabServiceException;
import ru.bmstu.labs.customdb.model.User;
import ru.bmstu.labs.customdb.repository.UserRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class UserService extends DatabaseService {

    //    @Value("${output.path.logging.user}")
    private final String loggingFile = "data/transaction_logs_user.txt";

//    private List<String> transactionLogs;

    private UserRepository userRepository;

    private ObjectMapper mapper = new ObjectMapper();
    private HashMap<Long, User> tempStorage;

    private Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository) throws LabServiceException {
        this.userRepository = userRepository;
        restoreLogs();
//        this.transactionLogs = new ArrayList<>();
    }

    /*public LaunchResponse launch() {
        try {
            LaunchResponse response = super.launch();
            restoreLogs();
            return response;
        } catch (LabServiceException e) {
            return new LaunchResponse("FAILED: " + e.getMessage());
        }
    }*/

    public List<User> getEntities() {
        return userRepository.findAll();
    }

    public User getEntity(Long id) throws LabServiceException {
        return userRepository.getById(id).orElseThrow(() ->
                new LabServiceException("User not found: id=" + id));
    }

    public User createEntity(UserCreateRequest request) throws LabServiceException {
        log.debug("method=createEntity message='Create request received: {}'", request);

        User user = new User();
        user.setId(request.getId());
        user.setName(request.getName());
        user.setLastName(request.getLastname());
        user.setEmail(request.getEmail());

        userRepository.save(user);

        log.debug("method=createEntity message='Entity created successfully'");
        return user;
    }

    public User updateEntity(UserUpdateRequest request) throws LabServiceException {
        log.debug("method=updateEntity message='Update request received: {}'", request);

        User user = userRepository.getById(request.getId()).orElseThrow(() ->
                new LabServiceException("User not found: id=" + request.getId()));

        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getLastname() != null) {
            user.setLastName(request.getLastname());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        user = userRepository.save(user);

        log.debug("method=updateEntity message='User updated successfully: {}'", user);
        return user;
    }

    public User deleteEntity(Long id) throws LabServiceException {
        log.debug("method=deleteEntity message='Delete request received: id={}'", id);

        User user = userRepository.deleteById(id).orElseThrow(() ->
                new LabServiceException("User not found: id=" + id));

        log.debug("method=deleteEntity message='User deleted: {}'", user);
        return user;
    }

    public User createEntityTemp(UserCreateRequest request) throws LabServiceException {
        log.debug("method=createEntity message='Create request received: {}'", request);

        if (tempStorage == null) {
            tempStorage = userRepository.getStorageCopy();
        }

        User user = new User();
        user.setId(request.getId());
        user.setName(request.getName());
        user.setLastName(request.getLastname());
        user.setEmail(request.getEmail());

        tempStorage.put(user.getId(), user);

        log.debug("method=createEntity message='Entity created successfully'");
        return user;
    }

    public User updateEntityTemp(UserUpdateRequest request) throws LabServiceException {
        log.debug("method=updateEntity message='Update request received: {}'", request);

        if (tempStorage == null) {
            tempStorage = userRepository.getStorageCopy();
        }

        User user = userRepository.getById(request.getId()).orElseThrow(() ->
                new LabServiceException("User not found: id=" + request.getId()));

        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getLastname() != null) {
            user.setLastName(request.getLastname());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        user = tempStorage.put(user.getId(), user);

        log.debug("method=updateEntity message='User updated successfully: {}'", user);
        return user;
    }

    public User deleteEntityTemp(Long id) throws LabServiceException {
        log.debug("method=deleteEntity message='Delete request received: id={}'", id);

        User user = userRepository.deleteById(id).orElseThrow(() ->
                new LabServiceException("User not found: id=" + id));

        log.debug("method=deleteEntity message='User deleted: {}'", user);
        return user;
    }

    public User transaction(List<TransactionRequest> requestList) throws LabServiceException {
        log.debug("method=transaction message='Requests received: {}'", requestList);
        saveRequestsToLogs(requestList);

        try {
            for (TransactionRequest transactionRequest : requestList) {
                switch (transactionRequest.getOperationName().toUpperCase()) {
                    case "INSERT":
                        createEntityTemp(mapper.readValue(transactionRequest.getData(), UserCreateRequest.class));
                        break;
                    case "UPDATE":
                        updateEntityTemp(mapper.readValue(transactionRequest.getData(), UserUpdateRequest.class));
                        break;
                    case "DELETE":
                        deleteEntityTemp(Long.parseLong(transactionRequest.getData()));
                        break;
                    default:
                        throw new LabServiceException("Operation is not supported: " + transactionRequest.getOperationName().toUpperCase());
                }

                /*transactionLogs.add(mapper.writeValueAsString(transactionRequest));

                if (!isActivate()) {
                    saveLogsToOutputFile();
                    return null;
                }*/
                Thread.sleep(5 * 1000);
            }
        } catch (InterruptedException e) {
            log.error("method=transaction message='Exception while trying to sleep'");
            throw new LabServiceException("Internal error");
        } catch (JsonProcessingException e) {
            throw new LabServiceException("Invalid JSON format: " + e.getMessage());
        }

//        transactionLogs.clear();
        commit();
        clearLogs();
        return null;
    }

    public void commit() {


    }

    private void saveRequestsToLogs(List<TransactionRequest> requestList) throws LabServiceException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(loggingFile));
            for (TransactionRequest request : requestList) {
                writer.append(mapper.writeValueAsString(request)).append("\n");
            }
            writer.close();

            log.debug("Logs saved successfully");
        } catch (IOException e) {
            throw new LabServiceException("Failed to save logs: " + e.getMessage());
        }
    }

    /*private void saveLogsToOutputFile() throws LabServiceException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(loggingFile));
            for (String log : transactionLogs) {
                writer.append(log).append("\n");
            }
            writer.close();
            log.debug("Logs saved successfully");
        } catch (IOException e) {
            throw new LabServiceException("Failed to save logs: " + e.getMessage());
        }
    }*/

    private void restoreLogs() throws LabServiceException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(loggingFile));
            List<TransactionRequest> requestsFromLogs = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) {
                requestsFromLogs.add(mapper.readValue(line, TransactionRequest.class));
            }
            log.info("Logs recovery...");
            if (!requestsFromLogs.isEmpty()) {
                transaction(requestsFromLogs);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            log.warn("User logs not found");
        } catch (IOException e) {
            throw new LabServiceException("Failed to parse logs: " + e.getMessage());
        }
    }

    private void clearLogs() {
        File logs = new File(loggingFile);
        logs.delete();
        log.debug("Logs cleared successfully");
    }
}
