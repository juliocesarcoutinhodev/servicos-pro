package com.servicepro.providers.interfaces.mapper;

import com.servicepro.providers.domain.model.ProviderReview;
import com.servicepro.providers.interfaces.dto.ProviderReviewResponse;
import com.servicepro.shared.infrastructure.mapping.MapStructCentralConfig;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(config = MapStructCentralConfig.class)
public interface ProviderReviewResponseMapper {

    ProviderReviewResponse toResponse(ProviderReview providerReview);

    List<ProviderReviewResponse> toResponseList(List<ProviderReview> providerReviews);
}
