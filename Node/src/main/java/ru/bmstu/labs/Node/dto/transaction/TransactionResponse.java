package ru.bmstu.labs.Node.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.bmstu.labs.Node.dto.LabDTO;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse implements LabDTO {
    private String message;
}
