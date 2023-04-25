package ru.bmstu.labs.MainNode.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.bmstu.labs.MainNode.dto.LabDTO;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class TransactionResponse implements LabDTO {
    private String message;
}
