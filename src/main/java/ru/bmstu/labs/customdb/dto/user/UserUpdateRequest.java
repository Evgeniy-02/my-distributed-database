package ru.bmstu.labs.customdb.dto.user;

import io.leangen.graphql.annotations.GraphQLNonNull;
import lombok.Data;

@Data
public class UserUpdateRequest extends UserRequest {

    @Override
    public void setId(@GraphQLNonNull Long id) {
        super.setId(id);
    }
}
