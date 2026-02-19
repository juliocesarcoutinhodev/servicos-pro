package com.servicepro.auth.interfaces.mapper;

import com.servicepro.auth.domain.model.User;
import com.servicepro.auth.interfaces.dto.UserResponse;
import com.servicepro.shared.infrastructure.mapping.MapStructCentralConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructCentralConfig.class)
public interface UserMapper {

    UserResponse toUserResponse(User user);
}
