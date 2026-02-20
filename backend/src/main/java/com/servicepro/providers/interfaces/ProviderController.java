package com.servicepro.providers.interfaces;

import com.servicepro.providers.application.usecase.getproviderprofile.GetProviderProfileCommand;
import com.servicepro.providers.application.usecase.getproviderprofile.GetProviderProfileUseCase;
import com.servicepro.providers.application.usecase.listproviders.ListProvidersCommand;
import com.servicepro.providers.application.usecase.listproviders.ListProvidersUseCase;
import com.servicepro.providers.application.usecase.listproviderreviews.ListProviderReviewsCommand;
import com.servicepro.providers.application.usecase.listproviderreviews.ListProviderReviewsUseCase;
import com.servicepro.providers.domain.model.ProviderPublicProfile;
import com.servicepro.providers.domain.model.ProviderReview;
import com.servicepro.providers.domain.model.ProviderSummary;
import com.servicepro.providers.interfaces.dto.ProviderProfileResponse;
import com.servicepro.providers.interfaces.dto.ProviderReviewResponse;
import com.servicepro.providers.interfaces.dto.ProviderSummaryResponse;
import com.servicepro.providers.interfaces.mapper.ProviderProfileResponseMapper;
import com.servicepro.providers.interfaces.mapper.ProviderReviewResponseMapper;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/providers")
@RequiredArgsConstructor
public class ProviderController {

    private final ListProvidersUseCase listProvidersUseCase;
    private final GetProviderProfileUseCase getProviderProfileUseCase;
    private final ListProviderReviewsUseCase listProviderReviewsUseCase;
    private final ProviderSummaryResponseMapper providerSummaryResponseMapper;
    private final ProviderProfileResponseMapper providerProfileResponseMapper;
    private final ProviderReviewResponseMapper providerReviewResponseMapper;

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

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProviderProfileResponse>> getProviderProfile(@PathVariable("id") UUID providerId) {
        ProviderPublicProfile providerPublicProfile = getProviderProfileUseCase.execute(new GetProviderProfileCommand(providerId));
        ProviderProfileResponse response = providerProfileResponseMapper.toResponse(providerPublicProfile);

        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK, "Perfil do prestador carregado com sucesso.", response));
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<ApiResponse<PageResponse<ProviderReviewResponse>>> listProviderReviews(
            @PathVariable("id") UUID providerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        ListProviderReviewsCommand command = new ListProviderReviewsCommand(providerId, page, size);
        Page<ProviderReview> reviewPage = listProviderReviewsUseCase.execute(command);
        List<ProviderReviewResponse> content = providerReviewResponseMapper.toResponseList(reviewPage.getContent());

        PageResponse<ProviderReviewResponse> response = new PageResponse<>(
                content,
                reviewPage.getTotalElements(),
                reviewPage.getNumber(),
                reviewPage.getSize()
        );

        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK, "Avaliacoes do prestador carregadas com sucesso.", response));
    }
}
