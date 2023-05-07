package ru.bmstu.labs.MainNode.dto.user;

import io.leangen.graphql.annotations.GraphQLIgnore;
import lombok.Data;
import ru.bmstu.labs.MainNode.dto.LabDTO;

@Data
public class UserRequest implements LabDTO {
    private String alias;
    private Long id;
    private String name;
    private String lastname;
    private String email;
    @GraphQLIgnore
    private Integer createdAt;
    @GraphQLIgnore
    private Integer updatedAt;
    @GraphQLIgnore
    private Integer deletedAt;
}
