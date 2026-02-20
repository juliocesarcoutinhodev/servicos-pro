package com.servicepro.catalog.interfaces;

import com.servicepro.catalog.application.usecase.createcategory.CreateServiceCategoryCommand;
import com.servicepro.catalog.application.usecase.createcategory.CreateServiceCategoryUseCase;
import com.servicepro.catalog.application.usecase.listcategories.ListServiceCategoriesUseCase;
import com.servicepro.catalog.domain.model.ServiceCategory;
import com.servicepro.catalog.interfaces.dto.CreateServiceCategoryRequest;
import com.servicepro.catalog.interfaces.dto.ServiceCategoryResponse;
import com.servicepro.catalog.interfaces.mapper.CreateServiceCategoryRequestMapper;
import com.servicepro.catalog.interfaces.mapper.ServiceCategoryResponseMapper;
import com.servicepro.shared.interfaces.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/services/categories")
@RequiredArgsConstructor
public class ServiceCategoryController {

    private final CreateServiceCategoryUseCase createServiceCategoryUseCase;
    private final ListServiceCategoriesUseCase listServiceCategoriesUseCase;
    private final CreateServiceCategoryRequestMapper createServiceCategoryRequestMapper;
    private final ServiceCategoryResponseMapper serviceCategoryResponseMapper;

    @PostMapping
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ServiceCategoryResponse>> createCategory(
            @Valid @RequestBody CreateServiceCategoryRequest request
    ) {
        CreateServiceCategoryCommand command = createServiceCategoryRequestMapper.toCommand(request);
        ServiceCategory createdCategory = createServiceCategoryUseCase.execute(command);
        ServiceCategoryResponse response = serviceCategoryResponseMapper.toResponse(createdCategory);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(HttpStatus.CREATED, "Categoria de servico cadastrada com sucesso.", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ServiceCategoryResponse>>> listCategories() {
        List<ServiceCategory> categories = listServiceCategoriesUseCase.execute();
        List<ServiceCategoryResponse> response = serviceCategoryResponseMapper.toResponseList(categories);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK, "Categorias de servico carregadas com sucesso.", response));
    }
}
