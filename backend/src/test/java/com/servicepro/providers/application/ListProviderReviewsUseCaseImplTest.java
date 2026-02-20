package com.servicepro.providers.application;

import com.servicepro.providers.application.usecase.listproviderreviews.ListProviderReviewsCommand;
import com.servicepro.providers.application.usecase.listproviderreviews.ListProviderReviewsUseCase;
import com.servicepro.providers.application.usecase.listproviderreviews.ListProviderReviewsUseCaseImpl;
import com.servicepro.providers.domain.exception.ProviderNotFoundException;
import com.servicepro.providers.domain.gateway.ProviderProfileGateway;
import com.servicepro.providers.domain.model.ProviderReview;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ListProviderReviewsUseCaseImplTest {

    @Mock
    private ProviderProfileGateway providerProfileGateway;

    private ListProviderReviewsUseCase listProviderReviewsUseCase;

    @BeforeEach
    void setUp() {
        listProviderReviewsUseCase = new ListProviderReviewsUseCaseImpl(providerProfileGateway);
    }

    @Test
    void shouldThrowNotFoundWhenProviderDoesNotExist() {
        UUID providerId = UUID.randomUUID();
        given(providerProfileGateway.existsActiveProviderById(providerId)).willReturn(false);

        assertThatThrownBy(() -> listProviderReviewsUseCase.execute(new ListProviderReviewsCommand(providerId, 0, 10)))
                .isInstanceOf(ProviderNotFoundException.class);
    }

    @Test
    void shouldNormalizePageAndSizeBeforeDelegating() {
        UUID providerId = UUID.randomUUID();
        ProviderReview review = new ProviderReview(
                UUID.randomUUID(),
                "Ana Paula",
                5,
                "Excelente profissional!",
                OffsetDateTime.now()
        );
        Page<ProviderReview> expectedPage = new PageImpl<>(
                List.of(review),
                PageRequest.of(0, 10),
                1
        );

        given(providerProfileGateway.existsActiveProviderById(providerId)).willReturn(true);
        given(providerProfileGateway.findActiveReviewsByProviderId(providerId, PageRequest.of(0, 10)))
                .willReturn(expectedPage);

        Page<ProviderReview> result = listProviderReviewsUseCase.execute(new ListProviderReviewsCommand(providerId, -1, 0));

        assertThat(result).isEqualTo(expectedPage);
        then(providerProfileGateway).should().findActiveReviewsByProviderId(providerId, PageRequest.of(0, 10));
    }

    @Test
    void shouldCapSizeAtMaxAllowedValue() {
        UUID providerId = UUID.randomUUID();
        Page<ProviderReview> expectedPage = new PageImpl<>(
                List.of(),
                PageRequest.of(1, 100),
                0
        );

        given(providerProfileGateway.existsActiveProviderById(providerId)).willReturn(true);
        given(providerProfileGateway.findActiveReviewsByProviderId(providerId, PageRequest.of(1, 100)))
                .willReturn(expectedPage);

        Page<ProviderReview> result = listProviderReviewsUseCase.execute(new ListProviderReviewsCommand(providerId, 1, 500));

        assertThat(result).isEqualTo(expectedPage);
        then(providerProfileGateway).should().findActiveReviewsByProviderId(providerId, PageRequest.of(1, 100));
    }
}
