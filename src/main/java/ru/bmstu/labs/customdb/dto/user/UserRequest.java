package ru.bmstu.labs.customdb.dto.user;

import lombok.Data;
import ru.bmstu.labs.customdb.dto.LabDTO;

@Data
public class UserRequest implements LabDTO {
    private Long id;
    private String name;
    private String lastname;
    private String email;
}
