package ru.bmstu.labs.customdb.dto.transaction;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    private String operationName;
    private String data;
}
