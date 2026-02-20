package com.servicepro.auth.interfaces.mapper;

import com.servicepro.auth.application.usecase.forgotpassword.ForgotPasswordCommand;
import com.servicepro.auth.interfaces.dto.ForgotPasswordRequest;
import com.servicepro.shared.infrastructure.mapping.MapStructCentralConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructCentralConfig.class)
public interface ForgotPasswordRequestMapper {

    ForgotPasswordCommand toCommand(ForgotPasswordRequest request);
}
