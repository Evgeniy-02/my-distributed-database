package ru.bmstu.labs.Node.dto.user;

import lombok.Data;
import ru.bmstu.labs.Node.dto.LabDTO;

@Data
public class UserRequest implements LabDTO {
    private String alias;
    private Long id;
    private String name;
    private String lastname;
    private String email;
    private Integer createdAt;
    private Integer updatedAt;
    private Integer deletedAt;
}
