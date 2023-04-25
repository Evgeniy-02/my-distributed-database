package ru.bmstu.labs.Node.dto.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserUpdateRequest extends UserRequest {

	@Override
	public void setAlias(@NonNull String alias) {
		super.setAlias(alias);
	}
	
    @Override
    public void setId(@NonNull Long id) {
        super.setId(id);
    }
}
