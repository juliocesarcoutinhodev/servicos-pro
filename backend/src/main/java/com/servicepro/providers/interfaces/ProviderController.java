package com.servicepro.providers.interfaces;

import com.servicepro.providers.application.usecase.listproviders.ListProvidersCommand;
import com.servicepro.providers.application.usecase.listproviders.ListProvidersUseCase;
import com.servicepro.providers.domain.model.ProviderSummary;
import com.servicepro.providers.interfaces.dto.ProviderSummaryResponse;
import com.servicepro.providers.interfaces.mapper.ProviderSummaryResponseMapper;
import com.servicepro.shared.interfaces.response.ApiResponse;
import com.servicepro.shared.interfaces.response.PageResponse;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/providers")
@RequiredArgsConstructor
public class ProviderController {

    private final ListProvidersUseCase listProvidersUseCase;
    private final ProviderSummaryResponseMapper providerSummaryResponseMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProviderSummaryResponse>>> listProviders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) UUID categoryId
    ) {
        ListProvidersCommand command = new ListProvidersCommand(page, size, categoryId);
        Page<ProviderSummary> providerPage = listProvidersUseCase.execute(command);
        List<ProviderSummaryResponse> content = providerSummaryResponseMapper.toResponseList(providerPage.getContent());

        PageResponse<ProviderSummaryResponse> response = new PageResponse<>(
                content,
                providerPage.getTotalElements(),
                providerPage.getNumber(),
                providerPage.getSize()
        );

        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK, "Prestadores carregados com sucesso.", response));
    }
}
