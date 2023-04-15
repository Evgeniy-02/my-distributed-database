package ru.bmstu.labs.customdb.dto.user;

import io.leangen.graphql.annotations.GraphQLNonNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserUpdateRequest extends UserRequest {

    @Override
    public void setId(@GraphQLNonNull Long id) {
        super.setId(id);
    }
}
