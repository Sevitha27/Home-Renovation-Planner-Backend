package com.lowes.service;

import com.lowes.dto.request.PhaseMaterialUserRequest;
import com.lowes.dto.response.PhaseMaterialUserResponse;
import com.lowes.entity.Material;
import com.lowes.entity.Phase;
import com.lowes.entity.PhaseMaterial;
import com.lowes.entity.enums.PhaseStatus;
import com.lowes.entity.enums.PhaseType;
import com.lowes.entity.enums.Unit;
import com.lowes.exception.ElementNotFoundException;
import com.lowes.exception.EmptyException;
import com.lowes.exception.OperationNotAllowedException;
import com.lowes.repository.MaterialRepository;
import com.lowes.repository.PhaseMaterialRepository;
import com.lowes.repository.PhaseRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PhaseMaterialServiceTests {

    @Mock
    PhaseMaterialRepository phaseMaterialRepository;

    @Mock
    PhaseRepository phaseRepository;

    @Mock
    MaterialRepository materialRepository;

    @Mock
    PhaseService phaseService;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    PhaseMaterialService phaseMaterialService;

    private Phase getPhase(){
        Phase phase = Phase.builder()
                .id(UUID.fromString("4dcb69c2-c5b9-4f8d-89e2-2ccf4ec5b808"))
                .phaseName("Foundation Work")
                .description("Phase involves laying the foundation")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 15))
                .phaseType(PhaseType.CIVIL)
                .requiredSkill(null)
                .vendorCost(50000)
                .totalPhaseMaterialCost(0)
                .phaseStatus(PhaseStatus.INPROGRESS)
                .room(null)
                .vendor(null)
                .build();

        return phase;
    }

    private Material getMaterial(){
        Material material = Material.builder()
                .id(1)
                .exposedId(UUID.fromString("975c9db9-7296-42a0-b5de-ebe1ab65857c"))
                .name("Cement")
                .unit(Unit.KG)
                .phaseType(PhaseType.CIVIL)
                .pricePerQuantity(100)
                .deleted(false)
                .build();

        return material;
    }

    private PhaseMaterial getPhaseMaterial(){

        Material material = getMaterial();

        Phase phase = getPhase();

        PhaseMaterial phaseMaterial = PhaseMaterial.builder()
                .id(1)
                .name("Cement")
                .exposedId(UUID.randomUUID())
                .unit(Unit.KG)
                .phaseType(PhaseType.CIVIL)
                .pricePerQuantity(100)
                .quantity(5)
                .totalPrice(500)
                .build();



        return phaseMaterial;
    }

    private PhaseMaterialUserRequest getPhaseMaterialUserRequest(){

        PhaseMaterialUserRequest phaseMaterialUserRequest = PhaseMaterialUserRequest.builder()
                .materialExposedId(UUID.fromString("975c9db9-7296-42a0-b5de-ebe1ab65857c"))
                .quantity(5)
                .build();

        return phaseMaterialUserRequest;
    }

    @Test
    public void addPhaseMaterialsToPhaseByPhaseId(){

        ReflectionTestUtils.setField(phaseMaterialService, "entityManager", entityManager);

        PhaseMaterialUserRequest phaseMaterialUserRequest = getPhaseMaterialUserRequest();

        Phase phase = getPhase();
        Material material = getMaterial();
        PhaseMaterial phaseMaterial = getPhaseMaterial();
        material.getPhaseMaterialList().add(phaseMaterial);
        phase.getPhaseMaterialList().add(phaseMaterial);
        phaseMaterial.setMaterial(material);
        phaseMaterial.setPhase(phase);

        Mockito.when(phaseRepository.findById(phase.getId())).thenReturn(Optional.of(phase));
        Mockito.when(materialRepository.findByExposedId(material.getExposedId())).thenReturn(Optional.of(material));
        Mockito.when(phaseRepository.save(any(Phase.class))).thenReturn(phase);
        Mockito.when(materialRepository.save(any(Material.class))).thenReturn(material);
        Mockito.when(phaseMaterialRepository.save(any(PhaseMaterial.class))).thenReturn(phaseMaterial);
        Mockito.when(phaseService.calculateTotalCost(phase.getId())).thenReturn(1);

        List<PhaseMaterialUserResponse> phaseMaterialUserResponseList = phaseMaterialService.addPhaseMaterialsToPhaseByPhaseId(phase.getId(),List.of(phaseMaterialUserRequest));

        Assertions.assertNotNull(phaseMaterialUserResponseList);
        Assertions.assertEquals(1,phaseMaterialUserResponseList.size());
        Assertions.assertEquals("Cement",phaseMaterialUserResponseList.getFirst().getName());
    }

    @Test
    public void addPhaseMaterialsToPhaseByPhaseId_ShouldThrowExceptionWhenPhaseNotFound() {
        UUID phaseId = UUID.randomUUID();

        Mockito.when(phaseRepository.findById(phaseId)).thenReturn(Optional.empty());

        List<PhaseMaterialUserRequest> requestList = List.of(getPhaseMaterialUserRequest());

        Assertions.assertThrows(ElementNotFoundException.class, () ->
                phaseMaterialService.addPhaseMaterialsToPhaseByPhaseId(phaseId, requestList));
    }

    @Test
    void addPhaseMaterialsToPhaseByPhaseId_ShouldThrowExceptionWhenPhaseMaterialRequestListIsEmpty() {
        Phase phase = getPhase();
        UUID phaseId = phase.getId();

        Mockito.when(phaseRepository.findById(phaseId)).thenReturn(Optional.of(phase));

        Assertions.assertThrows(EmptyException.class, () ->
                phaseMaterialService.addPhaseMaterialsToPhaseByPhaseId(phaseId, List.of()));
    }

    @Test
    void addPhaseMaterialsToPhaseByPhaseId_ShouldThrowExceptionWhenQuantityIsZero() {

        Phase phase = getPhase();
        UUID phaseId = phase.getId();

        PhaseMaterialUserRequest invalidRequest = getPhaseMaterialUserRequest();
        invalidRequest.setQuantity(0);

        Mockito.when(phaseRepository.findById(phaseId)).thenReturn(Optional.of(phase));

        Assertions.assertThrows(OperationNotAllowedException.class, () ->
                phaseMaterialService.addPhaseMaterialsToPhaseByPhaseId(phaseId, List.of(invalidRequest)));
    }

    @Test
    void addPhaseMaterialsToPhaseByPhaseId_ShouldThrowExceptionWhenQuantityIsNegative() {

        Phase phase = getPhase();
        UUID phaseId = phase.getId();

        PhaseMaterialUserRequest invalidRequest = getPhaseMaterialUserRequest();
        invalidRequest.setQuantity(-1);

        Mockito.when(phaseRepository.findById(phaseId)).thenReturn(Optional.of(phase));

        Assertions.assertThrows(OperationNotAllowedException.class, () ->
                phaseMaterialService.addPhaseMaterialsToPhaseByPhaseId(phaseId, List.of(invalidRequest)));
    }

    @Test
    void addPhaseMaterialsToPhaseByPhaseId_ShouldThrowExceptionWhenPhaseStatusIsNotStartedOrCompleted() {

        Phase phase = getPhase();
        phase.setPhaseStatus(PhaseStatus.COMPLETED);
        UUID phaseId = phase.getId();

        PhaseMaterialUserRequest request = getPhaseMaterialUserRequest();

        Mockito.when(phaseRepository.findById(phaseId)).thenReturn(Optional.of(phase));

        Assertions.assertThrows(OperationNotAllowedException.class, () ->
                phaseMaterialService.addPhaseMaterialsToPhaseByPhaseId(phaseId, List.of(request)));
    }

    @Test
    void addPhaseMaterialsToPhaseByPhaseId_ShouldThrowExceptionWhenMaterialNotFound() {
        Phase phase = getPhase();
        UUID phaseId = phase.getId();

        PhaseMaterialUserRequest request = getPhaseMaterialUserRequest();

        Mockito.when(phaseRepository.findById(phaseId)).thenReturn(Optional.of(phase));
        Mockito.when(materialRepository.findByExposedId(request.getMaterialExposedId())).thenReturn(Optional.empty());

        Assertions.assertThrows(ElementNotFoundException.class, () ->
                phaseMaterialService.addPhaseMaterialsToPhaseByPhaseId(phaseId, List.of(request)));
    }

    @Test
    public void addPhaseMaterialsToPhaseByPhaseId_ShouldThrowExceptionWhenMaterialPhaseTypeDoesNotMatchPhase() {
        Phase phase = getPhase();
        UUID phaseId = phase.getId();

        Material material = getMaterial();
        material.setPhaseType(PhaseType.PLUMBING);

        PhaseMaterialUserRequest request = getPhaseMaterialUserRequest();

        Mockito.when(phaseRepository.findById(phaseId)).thenReturn(Optional.of(phase));
        Mockito.when(materialRepository.findByExposedId(request.getMaterialExposedId())).thenReturn(Optional.of(material));

        Assertions.assertThrows(OperationNotAllowedException.class, () ->
                phaseMaterialService.addPhaseMaterialsToPhaseByPhaseId(phaseId, List.of(request)));
    }

    @Test
    public void updatePhaseMaterialQuantityByExposedId(){

        Phase phase = getPhase();
        Material material = getMaterial();
        PhaseMaterial phaseMaterial = getPhaseMaterial();
        material.getPhaseMaterialList().add(phaseMaterial);
        phase.getPhaseMaterialList().add(phaseMaterial);
        phaseMaterial.setMaterial(material);
        phaseMaterial.setPhase(phase);

        int pricePerQuantity = phaseMaterial.getPricePerQuantity();

        Mockito.when(phaseMaterialRepository.findByExposedId(phaseMaterial.getExposedId())).thenReturn(Optional.of(phaseMaterial));
        Mockito.when(phaseMaterialRepository.save(any(PhaseMaterial.class))).thenReturn(phaseMaterial);
        Mockito.when(phaseService.calculateTotalCost(phase.getId())).thenReturn(1);

        PhaseMaterialUserResponse phaseMaterialUserResponse = phaseMaterialService.updatePhaseMaterialQuantityByExposedId(phaseMaterial.getExposedId(),100);

        Assertions.assertNotNull(phaseMaterialUserResponse);
        Assertions.assertEquals(100,phaseMaterialUserResponse.getQuantity());
        Assertions.assertEquals(100*pricePerQuantity,phaseMaterialUserResponse.getTotalPrice());
        Assertions.assertEquals("Cement",phaseMaterialUserResponse.getName());
    }

    @Test
    public void updatePhaseMaterialQuantityByExposedId_ShouldThrowExceptionWhenPhaseMaterialIsNotFound(){

        Mockito.when(phaseMaterialRepository.findByExposedId(any(UUID.class))).thenReturn(Optional.empty());

        Assertions.assertThrows(ElementNotFoundException.class,()->{
            PhaseMaterialUserResponse phaseMaterialUserResponse = phaseMaterialService.updatePhaseMaterialQuantityByExposedId(UUID.randomUUID(),100);
        });
    }

    @Test
    public void updatePhaseMaterialQuantityByExposedId_ShouldThrowExceptionWhenQuantityIsZero(){

        Phase phase = getPhase();
        Material material = getMaterial();
        PhaseMaterial phaseMaterial = getPhaseMaterial();
        material.getPhaseMaterialList().add(phaseMaterial);
        phase.getPhaseMaterialList().add(phaseMaterial);
        phaseMaterial.setMaterial(material);
        phaseMaterial.setPhase(phase);

        Assertions.assertThrows(IllegalArgumentException.class,()->{
            PhaseMaterialUserResponse phaseMaterialUserResponse = phaseMaterialService.updatePhaseMaterialQuantityByExposedId(phaseMaterial.getExposedId(),0);
        });

    }

    @Test
    public void updatePhaseMaterialQuantityByExposedId_ShouldThrowExceptionWhenQuantityIsNegative(){

        Phase phase = getPhase();
        Material material = getMaterial();
        PhaseMaterial phaseMaterial = getPhaseMaterial();
        material.getPhaseMaterialList().add(phaseMaterial);
        phase.getPhaseMaterialList().add(phaseMaterial);
        phaseMaterial.setMaterial(material);
        phaseMaterial.setPhase(phase);

        Assertions.assertThrows(IllegalArgumentException.class,()->{
            PhaseMaterialUserResponse phaseMaterialUserResponse = phaseMaterialService.updatePhaseMaterialQuantityByExposedId(phaseMaterial.getExposedId(),-1);
        });

    }

    @Test
    public void updatePhaseMaterialQuantityByExposedId_ShouldThrowExceptionWhenPhaseStatusIsNotStartedOrCompleted() {

        Phase phase = getPhase();
        phase.setPhaseStatus(PhaseStatus.COMPLETED);
        Material material = getMaterial();
        PhaseMaterial phaseMaterial = getPhaseMaterial();
        material.getPhaseMaterialList().add(phaseMaterial);
        phase.getPhaseMaterialList().add(phaseMaterial);
        phaseMaterial.setMaterial(material);
        phaseMaterial.setPhase(phase);

        Mockito.when(phaseMaterialRepository.findByExposedId(phaseMaterial.getExposedId())).thenReturn(Optional.of(phaseMaterial));

        Assertions.assertThrows(OperationNotAllowedException.class,()->{
            PhaseMaterialUserResponse phaseMaterialUserResponse = phaseMaterialService.updatePhaseMaterialQuantityByExposedId(phaseMaterial.getExposedId(),100);
        });
    }

    @Test
    public void deletePhaseMaterialByExposedId(){

        Phase phase = getPhase();
        Material material = getMaterial();
        PhaseMaterial phaseMaterial = getPhaseMaterial();
        material.getPhaseMaterialList().add(phaseMaterial);
        phase.getPhaseMaterialList().add(phaseMaterial);
        phaseMaterial.setMaterial(material);
        phaseMaterial.setPhase(phase);

        Mockito.when(phaseMaterialRepository.findByExposedId(phaseMaterial.getExposedId())).thenReturn(Optional.of(phaseMaterial));
        Mockito.doNothing().when(phaseMaterialRepository).deleteByExposedId(phaseMaterial.getExposedId());
        Mockito.when(phaseService.calculateTotalCost(phase.getId())).thenReturn(1);
        PhaseMaterialUserResponse phaseMaterialUserResponse = phaseMaterialService.deletePhaseMaterialByExposedId(phaseMaterial.getExposedId());

        Assertions.assertNotNull(phaseMaterialUserResponse);
        Assertions.assertEquals("Cement",phaseMaterialUserResponse.getName());

    }

    @Test
    public void deletePhaseMaterialByExposedId_ShouldThrowExceptionWhenPhaseMaterialIsNotFound(){
        Mockito.when(phaseMaterialRepository.findByExposedId(any(UUID.class))).thenReturn(Optional.empty());

        Assertions.assertThrows(ElementNotFoundException.class,()->{
            PhaseMaterialUserResponse phaseMaterialUserResponse = phaseMaterialService.deletePhaseMaterialByExposedId(UUID.randomUUID());
        });
    }

    @Test
    public void deletePhaseMaterialByExposedId_ShouldThrowExceptionWhenPhaseStatusIsNotStartedOrCompleted() {

        Phase phase = getPhase();
        phase.setPhaseStatus(PhaseStatus.COMPLETED);
        Material material = getMaterial();
        PhaseMaterial phaseMaterial = getPhaseMaterial();
        material.getPhaseMaterialList().add(phaseMaterial);
        phase.getPhaseMaterialList().add(phaseMaterial);
        phaseMaterial.setMaterial(material);
        phaseMaterial.setPhase(phase);

        Mockito.when(phaseMaterialRepository.findByExposedId(phaseMaterial.getExposedId())).thenReturn(Optional.of(phaseMaterial));

        Assertions.assertThrows(OperationNotAllowedException.class,()->{
            PhaseMaterialUserResponse phaseMaterialUserResponse = phaseMaterialService.deletePhaseMaterialByExposedId(phaseMaterial.getExposedId());
        });

    }






}
