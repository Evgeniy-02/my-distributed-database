package ru.bmstu.labs.customdb.service;

import org.springframework.stereotype.Service;
import ru.bmstu.labs.customdb.dto.transaction.TransactionResponse;
import ru.bmstu.labs.customdb.issue.LabRepositoryException;
import ru.bmstu.labs.customdb.issue.LabServiceException;
import ru.bmstu.labs.customdb.repository.UserRepository;

@Service
public class DatabaseService {

    private final UserRepository userRepository;

    private boolean transactionMode = false;

    public DatabaseService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isTransactionMode() {
        return this.transactionMode;
    }

    public TransactionResponse beginTransaction() {
        if (!transactionMode) {
            this.transactionMode = true;
            userRepository.beginTransaction();
            return new TransactionResponse("Transaction begin completed successfully");
        } else {
            return new TransactionResponse("Transaction already started");
        }
    }

    public TransactionResponse commitTransaction() throws LabServiceException {
        try {
            if (transactionMode) {
                this.transactionMode = false;
                userRepository.commitTransaction();
                return new TransactionResponse("Transaction commit completed successfully");
            } else {
                return new TransactionResponse("Transaction is not begin");
            }
        } catch (LabRepositoryException e) {
            throw new LabServiceException(e.getMessage());
        }
    }

    public TransactionResponse rollbackTransaction() {
        if (transactionMode) {
            this.transactionMode = false;
            userRepository.rollbackTransaction();
            return new TransactionResponse("Transaction rollback completed successfully");
        } else {
            return new TransactionResponse("Transaction is not begin");
        }
    }
}
