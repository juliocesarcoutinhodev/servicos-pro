package com.servicepro.auth.interfaces.mapper;

import com.servicepro.auth.application.usecase.login.LoginCommand;
import com.servicepro.auth.interfaces.dto.LoginRequest;
import com.servicepro.shared.infrastructure.mapping.MapStructCentralConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructCentralConfig.class)
public interface LoginRequestMapper {

    LoginCommand toCommand(LoginRequest request);
}
