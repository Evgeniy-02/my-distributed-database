package ru.bmstu.labs.Node.dto.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
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
	public void setAlias(@NonNull String alias) {
		super.setAlias(alias);
	}

    @Override
    public void setName(@NonNull String name) {
        super.setName(name);
    }

    @Override
    public void setEmail(@NonNull String email) {
        super.setEmail(email);
    }
}
