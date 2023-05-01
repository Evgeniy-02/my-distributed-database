package ru.bmstu.labs.Node.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.bmstu.labs.Node.dto.transaction.Transaction;
import ru.bmstu.labs.Node.dto.user.*;
import ru.bmstu.labs.Node.issue.LabRepositoryException;
import ru.bmstu.labs.Node.issue.LabServiceException;
import ru.bmstu.labs.Node.model.User;
import ru.bmstu.labs.Node.repository.UserRepository;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Value("${server.port}")
    private String serverPort;

    private static final String DATA_DIRECTORY = "data/";
    private static final String LOG_FILE = "_transaction_logs_user.txt";

    private final UserRepository userRepository;

    private final DatabaseService databaseService;

    private final ObjectMapper mapper = new ObjectMapper();

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, DatabaseService databaseService) throws LabServiceException {
        this.userRepository = userRepository;
        this.databaseService = databaseService;
        restoreLogs();
    }

    private void restoreLogs() throws LabServiceException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(DATA_DIRECTORY + serverPort + LOG_FILE));
            String line;

            while ((line = reader.readLine()) != null) {
                Transaction transaction = mapper.readValue(line, Transaction.class);
                if (transaction.getOperationName().equalsIgnoreCase("save")) {
                    userRepository.save(mapper.readValue(transaction.getData(), User.class), false);
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
        log.debug("method=getEntities message='All users requested: alias={}'", alias);

        List<User> users = userRepository.findAll(alias);

        log.debug("method-getEntities message='Users requested successfully: {}'", users);
        return users;
    }

    public User getEntity(UserGetRequest request) throws LabServiceException {
        return userRepository.getById(request.getAlias(), request.getId()).orElseThrow(() ->
                new LabServiceException("User not found: id=" + request.getId()));
    }

    public User createEntity(UserCreateRequest request) throws LabServiceException {
        try {
            log.debug("method=createEntity message='Create request received: {}'", request);

            Optional<User> optionalUser = userRepository.getById(request.getAlias(), request.getId());
            if (optionalUser.isPresent()) {
                throw new LabServiceException("User already existed: id=" + request.getId());
            }

            User user = new User();
            user.setId(request.getId());
            user.setName(request.getName());
            user.setLastName(request.getLastname());
            user.setEmail(request.getEmail());

            user.setCreatedAt(Instant.now().getNano());
            user.setUpdatedAt(null);
            user.setDeletedAt(null);

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
            if (user.getDeletedAt() != null) {
                log.debug("method=updateEntity message='User already deleted: id={}'", user.getId());
                return user;
            }

            if (request.getName() != null) {
                user.setName(request.getName());
            }
            if (request.getLastname() != null) {
                user.setLastName(request.getLastname());
            }
            if (request.getEmail() != null) {
                user.setEmail(request.getEmail());
            }

            user.setUpdatedAt(Instant.now().getNano());

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
            if (user.getDeletedAt() != null) {
                log.debug("method=deleteEntity message='User already deleted: id={}'", user.getId());
                return user;
            }

            user.setDeletedAt(Instant.now().getNano());

            userRepository.save(user, databaseService.isTransactionMode(request.getAlias()));

            log.debug("method=deleteEntity message='User deleted: {}'", user);
            return user;
        } catch (LabRepositoryException e) {
            throw new LabServiceException(e.getMessage());
        }
    }

    public User syncEntity(UserRequest request) throws LabServiceException {
        try {
            log.debug("method=syncEntity message='Synchronization user request received: id={}'", request.getId());

            User user = new User();

            user.setId(request.getId());
            user.setName(request.getName());
            user.setLastName(request.getLastname());
            user.setEmail(request.getEmail());
            user.setCreatedAt(request.getCreatedAt());
            user.setUpdatedAt(request.getUpdatedAt());
            user.setDeletedAt(request.getDeletedAt());

            userRepository.save(user, databaseService.isTransactionMode(request.getAlias()));

            log.debug("method=syncEntity message='User synchronized: {}'", user);
            return user;
        } catch (LabRepositoryException e) {
            throw new LabServiceException(e.getMessage());
        }
    }
}
