package com.servicepro.providers.interfaces;

import com.servicepro.auth.domain.exception.InvalidCredentialsException;
import com.servicepro.auth.infrastructure.security.AuthenticatedUserPrincipal;
import com.servicepro.providers.application.usecase.createproviderservice.CreateProviderServiceCommand;
import com.servicepro.providers.application.usecase.createproviderservice.CreateProviderServiceUseCase;
import com.servicepro.providers.application.usecase.deleteproviderservice.DeleteProviderServiceCommand;
import com.servicepro.providers.application.usecase.deleteproviderservice.DeleteProviderServiceUseCase;
import com.servicepro.providers.application.usecase.listproviderservices.ListProviderServicesCommand;
import com.servicepro.providers.application.usecase.listproviderservices.ListProviderServicesUseCase;
import com.servicepro.providers.application.usecase.updateproviderservice.UpdateProviderServiceCommand;
import com.servicepro.providers.application.usecase.updateproviderservice.UpdateProviderServiceUseCase;
import com.servicepro.providers.domain.model.ProviderService;
import com.servicepro.providers.interfaces.dto.CreateProviderServiceRequest;
import com.servicepro.providers.interfaces.dto.ProviderServiceResponse;
import com.servicepro.providers.interfaces.dto.UpdateProviderServiceRequest;
import com.servicepro.providers.interfaces.mapper.CreateProviderServiceRequestMapper;
import com.servicepro.providers.interfaces.mapper.ProviderServiceResponseMapper;
import com.servicepro.providers.interfaces.mapper.UpdateProviderServiceRequestMapper;
import com.servicepro.shared.interfaces.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/providers/services")
@PreAuthorize("hasRole('PROVIDER')")
@RequiredArgsConstructor
public class ProviderServiceController {

    private final CreateProviderServiceUseCase createProviderServiceUseCase;
    private final ListProviderServicesUseCase listProviderServicesUseCase;
    private final UpdateProviderServiceUseCase updateProviderServiceUseCase;
    private final DeleteProviderServiceUseCase deleteProviderServiceUseCase;
    private final CreateProviderServiceRequestMapper createProviderServiceRequestMapper;
    private final UpdateProviderServiceRequestMapper updateProviderServiceRequestMapper;
    private final ProviderServiceResponseMapper providerServiceResponseMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<ProviderServiceResponse>> create(
            Authentication authentication,
            @Valid @RequestBody CreateProviderServiceRequest request
    ) {
        UUID providerId = getAuthenticatedProviderId(authentication);
        CreateProviderServiceCommand command = createProviderServiceRequestMapper.toCommand(request, providerId);
        ProviderService providerService = createProviderServiceUseCase.execute(command);
        ProviderServiceResponse response = providerServiceResponseMapper.toResponse(providerService);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(HttpStatus.CREATED, "Servico cadastrado com sucesso.", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProviderServiceResponse>>> listMyServices(Authentication authentication) {
        UUID providerId = getAuthenticatedProviderId(authentication);
        List<ProviderService> providerServices = listProviderServicesUseCase.execute(
                new ListProviderServicesCommand(providerId)
        );
        List<ProviderServiceResponse> response = providerServiceResponseMapper.toResponseList(providerServices);

        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK, "Servicos carregados com sucesso.", response));
    }

    @PutMapping("/{serviceId}")
    public ResponseEntity<ApiResponse<ProviderServiceResponse>> update(
            Authentication authentication,
            @PathVariable UUID serviceId,
            @Valid @RequestBody UpdateProviderServiceRequest request
    ) {
        UUID providerId = getAuthenticatedProviderId(authentication);
        UpdateProviderServiceCommand command = updateProviderServiceRequestMapper.toCommand(request, providerId, serviceId);
        ProviderService providerService = updateProviderServiceUseCase.execute(command);
        ProviderServiceResponse response = providerServiceResponseMapper.toResponse(providerService);

        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK, "Servico atualizado com sucesso.", response));
    }

    @DeleteMapping("/{serviceId}")
    public ResponseEntity<Void> delete(
            Authentication authentication,
            @PathVariable UUID serviceId
    ) {
        UUID providerId = getAuthenticatedProviderId(authentication);
        deleteProviderServiceUseCase.execute(new DeleteProviderServiceCommand(providerId, serviceId));
        return ResponseEntity.noContent().build();
    }

    private UUID getAuthenticatedProviderId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUserPrincipal principal)) {
            throw new InvalidCredentialsException();
        }
        return principal.userId();
    }
}
