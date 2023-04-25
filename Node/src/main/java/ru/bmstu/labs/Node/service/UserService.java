package ru.bmstu.labs.Node.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.bmstu.labs.Node.dto.transaction.Transaction;
import ru.bmstu.labs.Node.dto.user.UserCreateRequest;
import ru.bmstu.labs.Node.dto.user.UserDeleteRequest;
import ru.bmstu.labs.Node.dto.user.UserGetRequest;
import ru.bmstu.labs.Node.dto.user.UserUpdateRequest;
import ru.bmstu.labs.Node.issue.LabRepositoryException;
import ru.bmstu.labs.Node.issue.LabServiceException;
import ru.bmstu.labs.Node.model.User;
import ru.bmstu.labs.Node.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Service
public class UserService {

    private final String logFile = "data/transaction_logs_user.txt";

    private UserRepository userRepository;

    private DatabaseService databaseService;

    private ObjectMapper mapper = new ObjectMapper();

    private Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, DatabaseService databaseService) throws LabServiceException {
        this.userRepository = userRepository;
        this.databaseService = databaseService;
        restoreLogs();
    }

    private void restoreLogs() throws LabServiceException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(logFile));
            String line;

            while ((line = reader.readLine()) != null) {
                Transaction transaction = mapper.readValue(line, Transaction.class);
                if (transaction.getOperationName().equalsIgnoreCase("save")) {
                    userRepository.save(mapper.readValue(transaction.getData(), User.class), false);
                } else if (transaction.getOperationName().equalsIgnoreCase("delete")) {
                    userRepository.delete("", mapper.readValue(transaction.getData(), User.class), false);
                } else {
                    log.warn("This type of operation not found: " + transaction.getOperationName());
                }
            }

            userRepository.clearLogs();
            reader.close();
        } catch (FileNotFoundException e) {
            log.warn("User logs not found");
        } catch (IOException e) {
            throw new LabServiceException("Failed to parse logs: " + e.getMessage());
        } catch (LabRepositoryException e) {
            throw new LabServiceException(e.getMessage());
        }
    }

    public List<User> getEntities(String alias) {
        return userRepository.findAll(alias);
    }

    public User getEntity(UserGetRequest request) throws LabServiceException {
        return userRepository.getById(request.getAlias(), request.getId()).orElseThrow(() ->
                new LabServiceException("User not found: id=" + request.getId()));
    }

    public User createEntity(UserCreateRequest request) throws LabServiceException {
        try {
            log.debug("method=createEntity message='Create request received: {}'", request);

            User user = new User();
            user.setId(request.getId());
            user.setName(request.getName());
            user.setLastName(request.getLastname());
            user.setEmail(request.getEmail());

            userRepository.save(user, databaseService.isTransactionMode(request.getAlias()));

            log.debug("method=createEntity message='Entity created successfully'");
            return user;
        } catch (LabRepositoryException e) {
            throw new LabServiceException(e.getMessage());
        }
    }

    public User updateEntity(UserUpdateRequest request) throws LabServiceException {
        try {
            log.debug("method=updateEntity message='Update request received: {}'", request);

            User user = userRepository.getById(request.getAlias(), request.getId()).orElseThrow(() ->
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
            user.locked(request.getAlias());

            user = userRepository.save(user, databaseService.isTransactionMode(request.getAlias()));
            log.debug("method=updateEntity message='User updated successfully: {}'", user);

            user.unlocked();
            userRepository.save(user, databaseService.isTransactionMode(request.getAlias()));

            return user;
        } catch (LabRepositoryException e) {
            throw new LabServiceException(e.getMessage());
        }
    }

    public User deleteEntity(UserDeleteRequest request) throws LabServiceException {
        try {
            log.debug("method=deleteEntity message='Delete request received: id={}'", request.getId());

            User user = userRepository.getById(request.getAlias(), request.getId()).orElseThrow(() ->
                    new LabServiceException("User not found: id=" + request.getId()));
            userRepository.delete(request.getAlias(), user, databaseService.isTransactionMode(request.getAlias()));

            log.debug("method=deleteEntity message='User deleted: {}'", user);
            return user;
        } catch (LabRepositoryException e) {
            throw new LabServiceException(e.getMessage());
        }
    }
}
