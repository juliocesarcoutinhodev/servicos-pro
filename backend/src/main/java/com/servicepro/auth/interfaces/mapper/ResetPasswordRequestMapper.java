package com.servicepro.auth.interfaces.mapper;

import com.servicepro.auth.application.usecase.resetpassword.ResetPasswordCommand;
import com.servicepro.auth.interfaces.dto.ResetPasswordRequest;
import com.servicepro.shared.infrastructure.mapping.MapStructCentralConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructCentralConfig.class)
public interface ResetPasswordRequestMapper {

    ResetPasswordCommand toCommand(ResetPasswordRequest request);
}
