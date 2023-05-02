package ru.bmstu.labs.Node.service;

import org.springframework.stereotype.Service;
import ru.bmstu.labs.Node.dto.transaction.TransactionResponse;
import ru.bmstu.labs.Node.issue.LabRepositoryException;
import ru.bmstu.labs.Node.issue.LabServiceException;
import ru.bmstu.labs.Node.repository.UserRepository;

import java.util.HashMap;

@Service
public class DatabaseService {

    private final UserRepository userRepository;

    private HashMap<String, Boolean> transactionMode = new HashMap<>();

    public DatabaseService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isTransactionMode(String alias) {
        if (this.transactionMode.get(alias) == null) {
            return false;
        } else {
            return this.transactionMode.get(alias);
        }
    }

    public TransactionResponse beginTransaction(String alias) {
        if (transactionMode.get(alias) == null) {
            this.transactionMode.put(alias, true);
            userRepository.beginTransaction(alias);
            return new TransactionResponse("Transaction begin completed successfully");
        } else {
            return new TransactionResponse("Transaction already started");
        }
    }

    public TransactionResponse commitTransaction(String alias) throws LabServiceException {
        try {
            if (transactionMode.getOrDefault(alias, false)) {
                this.transactionMode.remove(alias);
                userRepository.commitTransaction(alias, transactionMode.isEmpty());
                return new TransactionResponse("Transaction commit completed successfully");
            } else {
                return new TransactionResponse("Transaction is not begin");
            }
        } catch (LabRepositoryException e) {
            throw new LabServiceException(e.getMessage());
        }
    }
}
