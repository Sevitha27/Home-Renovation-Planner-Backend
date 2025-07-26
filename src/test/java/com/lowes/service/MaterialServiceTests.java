package com.lowes.service;

import com.lowes.dto.response.MaterialUserResponse;
import com.lowes.entity.Material;
import com.lowes.entity.enums.PhaseType;
import com.lowes.entity.enums.Unit;
import com.lowes.repository.MaterialRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MaterialServiceTests {

    @Mock
    MaterialRepository materialRepository;

    @InjectMocks
    MaterialService materialService;

    @Test
    public void getExistingMaterialsByPhaseType_MaterialsExist(){

        Material material = Material.builder()
                .id(1)
                .exposedId(UUID.randomUUID())
                .name("Cement")
                .unit(Unit.KG)
                .phaseType(PhaseType.CIVIL)
                .pricePerQuantity(100)
                .deleted(false)
                .build();



        Mockito.when(materialRepository.findByPhaseTypeAndDeleted(eq(PhaseType.CIVIL), eq(false), any(Sort.class))).thenReturn(List.of(material));

        List<MaterialUserResponse> materialUserResponseList = materialService.getExistingMaterialsByPhaseType(PhaseType.CIVIL);

        Assertions.assertNotNull(materialUserResponseList);
        Assertions.assertEquals(1,materialUserResponseList.size());
        Assertions.assertEquals("Cement",materialUserResponseList.getFirst().getName());
        verify(materialRepository).findByPhaseTypeAndDeleted(eq(PhaseType.CIVIL), eq(false), any(Sort.class));
    }

    @Test
    public void getExistingMaterialsByPhaseType_MaterialsDoNotExist(){

        Mockito.when(materialRepository.findByPhaseTypeAndDeleted(eq(PhaseType.CIVIL), eq(false), any(Sort.class))).thenReturn(List.of());

        List<MaterialUserResponse> materialUserResponseList = materialService.getExistingMaterialsByPhaseType(PhaseType.CIVIL);

        Assertions.assertNotNull(materialUserResponseList);
        Assertions.assertEquals(0,materialUserResponseList.size());
        verify(materialRepository).findByPhaseTypeAndDeleted(eq(PhaseType.CIVIL), eq(false), any(Sort.class));

    }

    @Test
    public void getExistingMaterialsByPhaseType_CheckSortArgument(){

        ArgumentCaptor<Sort> sortArgumentCaptor = ArgumentCaptor.forClass(Sort.class);

        Mockito.when(materialRepository.findByPhaseTypeAndDeleted(eq(PhaseType.CIVIL), eq(false), sortArgumentCaptor.capture())).thenReturn(List.of());

        List<MaterialUserResponse> materialUserResponseList = materialService.getExistingMaterialsByPhaseType(PhaseType.CIVIL);

        Sort capturedSort = sortArgumentCaptor.getValue();

        Assertions.assertNotNull(capturedSort);
        Assertions.assertEquals(Sort.by(Sort.Direction.ASC,"id"),capturedSort);



    }

}
