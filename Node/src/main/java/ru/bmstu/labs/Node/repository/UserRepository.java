package ru.bmstu.labs.Node.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import ru.bmstu.labs.Node.dto.transaction.Transaction;
import ru.bmstu.labs.Node.issue.LabRepositoryException;
import ru.bmstu.labs.Node.model.User;

import java.io.*;
import java.util.*;

@Repository
public class UserRepository {
    @Value("${server.port}")
    private int serverPort;

    private final String dataDirectory = "data/";
    private String databaseFile = "_database_user.txt";
    private String logFile = "_transaction_logs_user.txt";

    @Value("${server.port}")
        // different nodes have different file names
    void updateFilenames(int port) {
        databaseFile = port + databaseFile;
        logFile = port + logFile;
    }

    private HashMap<Long, User> globalStorage = new HashMap<>();
    private HashMap<Long, User> tempStorage = new HashMap<>();

    private ObjectMapper mapper = new ObjectMapper();

    private Logger log = LoggerFactory.getLogger(UserRepository.class);

    private UserRepository() throws LabRepositoryException {
        restoreDatabase();
    }

    private void restoreDatabase() throws LabRepositoryException {
        globalStorage = new HashMap<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(dataDirectory + databaseFile));
            String line;

            while ((line = reader.readLine()) != null) {
                User user = mapper.readValue(line, User.class);
                globalStorage.put(user.getId(), user);
            }

            reader.close();
        } catch (FileNotFoundException e) {
            log.warn("User database not found");
        } catch (IOException e) {
            throw new LabRepositoryException(e.getMessage());
        }
    }

    public List<User> findAll(String alias) {
        if (!tempStorage.isEmpty()) {
            return findAll(alias, tempStorage);
        } else {
            return findAll(alias, globalStorage);
        }
    }

    public Optional<User> getById(String alias, Long id) {
        if (tempStorage.isEmpty()) {
            return getById(alias, id, globalStorage);
        } else {
            return getById(alias, id, tempStorage);
        }
    }

    public User save(User user, boolean transaction) throws LabRepositoryException {
        try {
            User savedUser;
            if (transaction) {
                savedUser = save(user, tempStorage);
                addLogs(new Transaction("save", mapper.writeValueAsString(user)));
            } else {
                savedUser = save(user, globalStorage);
                saveToDatabase();
            }
            return savedUser;
        } catch (JsonProcessingException e) {
            throw new LabRepositoryException(e.getMessage());
        }
    }

    public Optional<User> delete(String alias, User user, boolean transaction) throws LabRepositoryException {
        try {
            Optional<User> deletedUser;
            if (transaction) {
                deletedUser = delete(alias, user, tempStorage);
                addLogs(new Transaction("delete", mapper.writeValueAsString(user)));
            } else {
                deletedUser = delete(alias, user, globalStorage);
                saveToDatabase();
            }
            return deletedUser;
        } catch (JsonProcessingException e) {
            throw new LabRepositoryException(e.getMessage());
        }
    }

    public void beginTransaction(String alias) {
        if (tempStorage == null || tempStorage.isEmpty()) {
            tempStorage = new HashMap<>(globalStorage);
        }
    }

    public void commitTransaction(String alias, boolean clearTempStorage) throws LabRepositoryException {
        globalStorage = new HashMap<>(tempStorage);
        if (clearTempStorage) {
            tempStorage.clear();
        }
        saveToDatabase();
        clearLogs();
    }

    private void saveToDatabase() throws LabRepositoryException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(dataDirectory + databaseFile));
            for (Map.Entry<Long, User> entry : globalStorage.entrySet()) {
                writer.append(mapper.writeValueAsString(entry.getValue())).append("\n");
            }
            writer.close();
        } catch (IOException ignored) {
            throw new LabRepositoryException("Failed to save to database: " + ignored.getMessage());
        }
    }

    public void clearLogs() throws LabRepositoryException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(dataDirectory + logFile));
            writer.close();
        } catch (IOException e) {
            throw new LabRepositoryException(e.getMessage());
        }
    }

    private void addLogs(Transaction transaction) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(dataDirectory + logFile, true));
            writer.append(mapper.writeValueAsString(transaction)).append("\n");
            writer.close();
        } catch (IOException e) {
            log.error("Failed to save logs: " + e.getMessage());
        }
    }

    private List<User> findAll(String alias, HashMap<Long, User> storage) {
        List<User> users = new ArrayList<>();
        for (Map.Entry<Long, User> entry : storage.entrySet()) {
            if (!entry.getValue().isLocked() || entry.getValue().getLockedBy().compareTo(alias) == 0) {
                users.add(entry.getValue());
            }
        }
        return users;
    }

    private Optional<User> getById(String alias, Long id, HashMap<Long, User> storage) {
        Optional<User> returnedValue = Optional.ofNullable(storage.get(id));
        if (returnedValue.isPresent()) {
            if (!returnedValue.get().isLocked() || returnedValue.get().getLockedBy().compareTo(alias) == 0) {
                return returnedValue;
            } else {
                return Optional.empty();
            }
        } else {
            return returnedValue;
        }
    }

    private User save(User user, HashMap<Long, User> storage) {
        storage.put(user.getId(), user);
        return user;
    }

    private Optional<User> delete(String alias, User user, HashMap<Long, User> storage) {
        return Optional.ofNullable(storage.remove(user.getId()));
    }
}
