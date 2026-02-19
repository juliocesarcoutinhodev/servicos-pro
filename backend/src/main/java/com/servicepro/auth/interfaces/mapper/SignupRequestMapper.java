package com.servicepro.auth.interfaces.mapper;

import com.servicepro.auth.application.usecase.signup.SignupCommand;
import com.servicepro.auth.interfaces.dto.SignupRequest;
import com.servicepro.shared.infrastructure.mapping.MapStructCentralConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructCentralConfig.class)
public interface SignupRequestMapper {

    SignupCommand toCommand(SignupRequest request);
}
