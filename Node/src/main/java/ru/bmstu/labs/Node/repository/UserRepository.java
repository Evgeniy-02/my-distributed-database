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

    private String serverPort;

    private static final String DATA_DIRECTORY = "data/";
    private static final String DATABASE_FILE = "_database_user.txt";
    private static final String LOG_FILE = "_transaction_logs_user.txt";

    private final ObjectMapper mapper = new ObjectMapper();

    private final Logger log = LoggerFactory.getLogger(UserRepository.class);

    private HashMap<Long, User> globalStorage = new HashMap<>();
    private HashMap<Long, User> tempStorage = new HashMap<>();

    private UserRepository(@Value("${server.port}") String serverPort) throws LabRepositoryException {
        this.serverPort = serverPort;
        restoreDatabase();
    }

    private void restoreDatabase() throws LabRepositoryException {
        globalStorage = new HashMap<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(DATA_DIRECTORY + serverPort + DATABASE_FILE));
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
            BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_DIRECTORY + serverPort + DATABASE_FILE));
            for (Map.Entry<Long, User> entry : globalStorage.entrySet()) {
                writer.append(mapper.writeValueAsString(entry.getValue())).append("\n");
            }
            writer.close();
        } catch (IOException e) {
            throw new LabRepositoryException("Failed to save to database: " + e.getMessage());
        }
    }

    public void clearLogs() throws LabRepositoryException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_DIRECTORY + serverPort + LOG_FILE));
            writer.close();
        } catch (IOException e) {
            throw new LabRepositoryException(e.getMessage());
        }
    }

    private void addLogs(Transaction transaction) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_DIRECTORY + serverPort + LOG_FILE, true));
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
            }
        }

        return Optional.empty();
    }

    private User save(User user, HashMap<Long, User> storage) {
        storage.put(user.getId(), user);
        return user;
    }
}
