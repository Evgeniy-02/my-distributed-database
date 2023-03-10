package ru.bmstu.labs.customdb.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.bmstu.labs.customdb.dto.LabDTO;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class TransactionResponse implements LabDTO {
    private String message;
}
