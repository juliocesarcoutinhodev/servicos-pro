package com.servicepro.catalog.interfaces;

import com.servicepro.catalog.application.usecase.listcategories.ListServiceCategoriesUseCase;
import com.servicepro.catalog.domain.model.ServiceCategory;
import com.servicepro.catalog.interfaces.dto.ServiceCategoryResponse;
import com.servicepro.catalog.interfaces.mapper.ServiceCategoryResponseMapper;
import com.servicepro.shared.interfaces.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/services/categories")
@RequiredArgsConstructor
public class ServiceCategoryController {

    private final ListServiceCategoriesUseCase listServiceCategoriesUseCase;
    private final ServiceCategoryResponseMapper serviceCategoryResponseMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ServiceCategoryResponse>>> listCategories() {
        List<ServiceCategory> categories = listServiceCategoriesUseCase.execute();
        List<ServiceCategoryResponse> response = serviceCategoryResponseMapper.toResponseList(categories);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK, "Categorias de servico carregadas com sucesso.", response));
    }
}
