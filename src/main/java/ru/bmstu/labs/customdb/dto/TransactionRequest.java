package ru.bmstu.labs.customdb.dto;

import lombok.Data;

@Data
public class TransactionRequest implements LabDTO {
    private String operationName;
    private String data;
}
