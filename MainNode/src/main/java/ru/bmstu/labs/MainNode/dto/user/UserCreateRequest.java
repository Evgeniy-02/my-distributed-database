package ru.bmstu.labs.MainNode.dto.user;

import io.leangen.graphql.annotations.GraphQLIgnore;
import io.leangen.graphql.annotations.GraphQLNonNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserCreateRequest extends UserRequest {

    /*@Override
    @GraphQLIgnore
    public void setId(Long id) {
    }*/
	
	@Override
	public void setAlias(@GraphQLNonNull String alias) {
		super.setAlias(alias);
	}

    @Override
    public void setName(@GraphQLNonNull String name) {
        super.setName(name);
    }

    @Override
    public void setEmail(@GraphQLNonNull String email) {
        super.setEmail(email);
    }
}
