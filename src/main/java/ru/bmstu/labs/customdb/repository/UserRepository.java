package ru.bmstu.labs.customdb.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;
import ru.bmstu.labs.customdb.model.User;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Repository
public class UserRepository extends LabRepository {

    private final String databaseFile = "data/database_user.txt";

    private HashMap<Long, User> globalStorage;
    private HashMap<Long, User> tempStorage;

    private ObjectMapper mapper = new ObjectMapper();

    public UserRepository() {
        this.globalStorage = new HashMap<>();
    }

    private User save(User user, HashMap<Long, User> storage) {
        storage.put(user.getId(), user);
        return user;
    }

    private Optional<User> getById(Long id, HashMap<Long, User> storage) {
        return Optional.of(storage.get(id));
    }

    private Optional<User> deleteById(Long id, HashMap<Long, User> storage) {
        return Optional.of(storage.remove(id));
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        for (Map.Entry<Long, User> entry : globalStorage.entrySet()) {
            users.add(entry.getValue());
        }
        return users;
    }

    public User save(User user) {
        return save(user, globalStorage);
    }

    public User save(User user, boolean temporary) {
        if (!temporary) {
            return save(user, globalStorage);
        } else {
            return save(user, tempStorage);
        }
    }

    public Optional<User> getById(Long id) {
        return getById(id, globalStorage);
    }

    public Optional<User> getById(Long id, boolean temporary) {
        if (!temporary) {
            return getById(id, globalStorage);
        } else {
            return getById(id, tempStorage);
        }
    }

    public Optional<User> deleteById(Long id) {
        return deleteById(id, globalStorage);
    }

    public Optional<User> deleteById(Long id, boolean temporary) {
        if (!temporary) {
            return deleteById(id, globalStorage);
        } else {
            return deleteById(id, tempStorage);
        }
    }

    public HashMap<Long, User> getStorageCopy() {
        return (HashMap<Long, User>) globalStorage.clone();
    }

    private void saveToDatabase() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(databaseFile));
            for (Map.Entry<Long, User> entry : globalStorage.entrySet()) {
                writer.append(mapper.writeValueAsString(entry.getValue())).append("\n");
            }
            writer.close();
        } catch (IOException ignored) {
//            throw new LabRepositoryException("Failed to save to database: " + ignored.getMessage());
        }
    }
}
