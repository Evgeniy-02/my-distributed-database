package ru.bmstu.labs.MainNode.dto.transaction;

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
