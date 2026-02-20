package com.servicepro.providers.application.usecase.listproviderreviews;

import com.servicepro.providers.domain.model.ProviderReview;
import org.springframework.data.domain.Page;

public interface ListProviderReviewsUseCase {

    Page<ProviderReview> execute(ListProviderReviewsCommand command);
}
