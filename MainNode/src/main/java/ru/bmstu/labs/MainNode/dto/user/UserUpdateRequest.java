package ru.bmstu.labs.MainNode.dto.user;

import io.leangen.graphql.annotations.GraphQLNonNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserUpdateRequest extends UserRequest {
	
	@Override
	public void setAlias(@GraphQLNonNull String alias) {
		super.setAlias(alias);
	}

    @Override
    public void setId(@GraphQLNonNull Long id) {
        super.setId(id);
    }
}
