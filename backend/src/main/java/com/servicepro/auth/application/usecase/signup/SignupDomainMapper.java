package com.servicepro.auth.application.usecase.signup;

import com.servicepro.auth.domain.model.SignupData;
import com.servicepro.auth.domain.model.valueobject.Email;
import com.servicepro.auth.domain.model.valueobject.Phone;
import com.servicepro.shared.infrastructure.mapping.MapStructCentralConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructCentralConfig.class)
public interface SignupDomainMapper {

    SignupData toSignupData(SignupCommand command);

    default Email mapEmail(String email) {
        return Email.of(email);
    }

    default Phone mapPhone(String phone) {
        return Phone.of(phone);
    }
}
