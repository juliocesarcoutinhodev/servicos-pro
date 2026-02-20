package com.servicepro.catalog.application;

import com.servicepro.catalog.application.usecase.createcategory.CreateServiceCategoryCommand;
import com.servicepro.catalog.application.usecase.createcategory.CreateServiceCategoryUseCase;
import com.servicepro.catalog.application.usecase.createcategory.CreateServiceCategoryUseCaseImpl;
import com.servicepro.catalog.domain.exception.ServiceCategoryAlreadyExistsException;
import com.servicepro.catalog.domain.gateway.ServiceCategoryGateway;
import com.servicepro.catalog.domain.model.ServiceCategory;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateServiceCategoryUseCaseImplTest {

    @Mock
    private ServiceCategoryGateway serviceCategoryGateway;

    private CreateServiceCategoryUseCase createServiceCategoryUseCase;

    @BeforeEach
    void setUp() {
        createServiceCategoryUseCase = new CreateServiceCategoryUseCaseImpl(serviceCategoryGateway);
    }

    @Test
    void shouldCreateCategoryWhenNameDoesNotExist() {
        CreateServiceCategoryCommand command = new CreateServiceCategoryCommand(
                "Mecanico",
                "Reparos automotivos"
        );

        when(serviceCategoryGateway.existsByNormalizedName("mecanico")).thenReturn(false);
        when(serviceCategoryGateway.save(any(ServiceCategory.class))).thenAnswer(invocation -> {
            ServiceCategory serviceCategory = invocation.getArgument(0);
            return ServiceCategory.restore(
                    UUID.randomUUID(),
                    serviceCategory.getName(),
                    serviceCategory.getNormalizedName(),
                    serviceCategory.getDescription(),
                    true,
                    OffsetDateTime.now(),
                    OffsetDateTime.now()
            );
        });

        ServiceCategory created = createServiceCategoryUseCase.execute(command);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Mecanico");
        assertThat(created.getNormalizedName()).isEqualTo("mecanico");

        then(serviceCategoryGateway).should().save(any(ServiceCategory.class));
    }

    @Test
    void shouldThrowConflictWhenCategoryAlreadyExists() {
        CreateServiceCategoryCommand command = new CreateServiceCategoryCommand(
                "Mecânico",
                "Descricao"
        );

        when(serviceCategoryGateway.existsByNormalizedName("mecanico")).thenReturn(true);

        assertThatThrownBy(() -> createServiceCategoryUseCase.execute(command))
                .isInstanceOf(ServiceCategoryAlreadyExistsException.class);

        then(serviceCategoryGateway).should(never()).save(any(ServiceCategory.class));
    }
}
