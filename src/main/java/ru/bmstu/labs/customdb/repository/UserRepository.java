package ru.bmstu.labs.customdb.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.bmstu.labs.customdb.dto.transaction.Transaction;
import ru.bmstu.labs.customdb.issue.LabRepositoryException;
import ru.bmstu.labs.customdb.model.User;

import java.io.*;
import java.util.*;

@Repository
public class UserRepository {

    private final String databaseFile = "data/database_user.txt";
    private final String logFile = "data/transaction_logs_user.txt";

    private HashMap<Long, User> globalStorage = new HashMap<>();
    private HashMap<Long, User> tempStorage;

    private ObjectMapper mapper = new ObjectMapper();

    private Logger log = LoggerFactory.getLogger(UserRepository.class);

    private UserRepository() throws LabRepositoryException {
        restoreDatabase();
    }

    private void restoreDatabase() throws LabRepositoryException {
        globalStorage = new HashMap<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(databaseFile));
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

    public List<User> findAll(boolean transaction) {
        if (transaction) {
            return findAll(tempStorage);
        } else {
            return findAll(globalStorage);
        }
    }

    public Optional<User> getById(Long id, boolean temporary) {
        if (!temporary) {
            return getById(id, globalStorage);
        } else {
            return getById(id, tempStorage);
        }
    }

    public User save(User user, boolean transaction) throws LabRepositoryException {
        try {
            if (transaction) {
                addLogs(new Transaction("save", mapper.writeValueAsString(user)));
                return save(user, tempStorage);
            } else {
                User savedUser = save(user, globalStorage);
                saveToDatabase();
                return savedUser;
            }
        } catch (JsonProcessingException e) {
            throw new LabRepositoryException(e.getMessage());
        }
    }

    public Optional<User> delete(User user, boolean transaction) throws LabRepositoryException {
        try {
            if (transaction) {
                addLogs(new Transaction("delete", mapper.writeValueAsString(user)));
                return delete(user, tempStorage);
            } else {
                Optional<User> deletedUser = delete(user, globalStorage);
                saveToDatabase();
                return deletedUser;
            }
        } catch (JsonProcessingException e) {
            throw new LabRepositoryException(e.getMessage());
        }
    }

    public void beginTransaction() {
        tempStorage = new HashMap<>(globalStorage);
    }

    public void commitTransaction() throws LabRepositoryException {
        globalStorage = new HashMap<>(tempStorage);
        saveToDatabase();
        clearLogs();
    }

    public void rollbackTransaction() {
        tempStorage.clear();
    }

    private void saveToDatabase() throws LabRepositoryException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(databaseFile));
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
            BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));
            writer.close();
        } catch (IOException e) {
            throw new LabRepositoryException(e.getMessage());
        }
    }

    private void addLogs(Transaction transaction) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
            writer.append(mapper.writeValueAsString(transaction)).append("\n");
            writer.close();
        } catch (IOException e) {
            log.error("Failed to save logs: " + e.getMessage());
        }
    }

    private List<User> findAll(HashMap<Long, User> storage) {
        List<User> users = new ArrayList<>();
        for (Map.Entry<Long, User> entry : storage.entrySet()) {
            users.add(entry.getValue());
        }
        return users;
    }

    private Optional<User> getById(Long id, HashMap<Long, User> storage) {
        return Optional.ofNullable(storage.get(id));
    }

    private User save(User user, HashMap<Long, User> storage) {
        storage.put(user.getId(), user);
        return user;
    }

    private Optional<User> delete(User user, HashMap<Long, User> storage) {
        return Optional.ofNullable(storage.remove(user.getId()));
    }
}
