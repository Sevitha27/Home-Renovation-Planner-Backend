package com.lowes.repository;

import com.lowes.entity.Material;
import com.lowes.entity.Phase;
import com.lowes.entity.PhaseMaterial;
import com.lowes.entity.enums.PhaseStatus;
import com.lowes.entity.enums.PhaseType;
import com.lowes.entity.enums.Unit;
import org.checkerframework.checker.lock.qual.EnsuresLockHeldIf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class PhaseMaterialRepositoryTests {

    @Autowired
    PhaseMaterialRepository phaseMaterialRepository;

    @Autowired
    MaterialRepository materialRepository;

    @Autowired
    PhaseRepository phaseRepository;

    private PhaseMaterial getPhaseMaterial(){

        Material material = Material.builder()
                .name("Cement")
                .exposedId(UUID.randomUUID())
                .unit(Unit.KG)
                .phaseType(PhaseType.CIVIL)
                .pricePerQuantity(100)
                .build();

        UUID materialExposedId = material.getExposedId();
        materialRepository.save(material);
        Material savedMaterial = materialRepository.findByExposedId(materialExposedId).get();

                Phase phase = Phase.builder()
                .phaseName("Foundation Work")
                .description("Phase involves laying the foundation")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 15))
                .phaseType(PhaseType.CIVIL)
                .requiredSkill(null)
                .vendorCost(50000)
                .totalPhaseMaterialCost(0)
                .phaseStatus(PhaseStatus.NOTSTARTED)
                .room(null)
                .vendor(null)
                .build();


        phaseRepository.save(phase);
        Phase savedPhase = phaseRepository.findAll().getFirst();

                PhaseMaterial phaseMaterial = PhaseMaterial.builder()
                .name("Cement")
                .exposedId(UUID.randomUUID())
                .unit(Unit.KG)
                .phaseType(PhaseType.CIVIL)
                .pricePerQuantity(100)
                .quantity(5)
                .totalPrice(500)
                .build();

        phaseMaterial.setMaterial(savedMaterial);
        phaseMaterial.setPhase(savedPhase);
        savedMaterial.getPhaseMaterialList().add(phaseMaterial);
        savedPhase.getPhaseMaterialList().add(phaseMaterial);




        return phaseMaterial;
    }

    @Test
    public void whenSavedPhaseMaterialReturnNonNullPhaseMaterial(){
        PhaseMaterial phaseMaterial = getPhaseMaterial();

        PhaseMaterial savedPhaseMaterial = phaseMaterialRepository.save(phaseMaterial);

        Assertions.assertNotNull(savedPhaseMaterial);
        Assertions.assertNotEquals(0,savedPhaseMaterial.getId());
        Assertions.assertNotNull(savedPhaseMaterial.getExposedId());
        Assertions.assertEquals("Cement",savedPhaseMaterial.getName());
        Assertions.assertNotNull(savedPhaseMaterial.getMaterial());
        Assertions.assertNotNull(savedPhaseMaterial.getPhase());
        Assertions.assertEquals(1,savedPhaseMaterial.getMaterial().getPhaseMaterialList().size());
        Assertions.assertEquals(1,savedPhaseMaterial.getPhase().getPhaseMaterialList().size());

    }

    @Test
    public void whenExposedIdIsNullThenThrowException(){
        PhaseMaterial phaseMaterial = getPhaseMaterial();
        phaseMaterial.setExposedId(null);

        Assertions.assertThrows(DataIntegrityViolationException.class, ()->{phaseMaterialRepository.save(phaseMaterial);});
    }

    @Test
    public void whenNameIsNullThenThrowException(){
        PhaseMaterial phaseMaterial = getPhaseMaterial();
        phaseMaterial.setName(null);

        Assertions.assertThrows(DataIntegrityViolationException.class, ()->{phaseMaterialRepository.save(phaseMaterial);});
    }

    @Test
    public void whenUnitIsNullThenThrowException(){
        PhaseMaterial phaseMaterial = getPhaseMaterial();
        phaseMaterial.setUnit(null);

        Assertions.assertThrows(DataIntegrityViolationException.class, ()->{phaseMaterialRepository.save(phaseMaterial);});
    }

    @Test
    public void whenPhaseTypeIsNullThenThrowException(){
        PhaseMaterial phaseMaterial = getPhaseMaterial();
        phaseMaterial.setPhaseType(null);

        Assertions.assertThrows(DataIntegrityViolationException.class, ()->{phaseMaterialRepository.save(phaseMaterial);});
    }

//    @Test
//    public void whenNameIsIsNotUniqueThenThrowException(){
//        PhaseMaterial phaseMaterial1 = getPhaseMaterial();
//        phaseMaterialRepository.save(phaseMaterial1);
//        PhaseMaterial phaseMaterial2 = getPhaseMaterial();
//        phaseMaterial2.getMaterial().setName("Wood");
//        materialRepository.save(phaseMaterial2.getMaterial());
//
//        Assertions.assertThrows(DataIntegrityViolationException.class, ()->{phaseMaterialRepository.save(phaseMaterial2);});
//    }

    @Test
    public void findByPhaseId(){
        PhaseMaterial phaseMaterial = getPhaseMaterial();

        UUID phaseId = phaseMaterial.getPhase().getId();

        phaseMaterialRepository.save(phaseMaterial);

        List<PhaseMaterial> phaseMaterialList = phaseMaterialRepository.findByPhaseId(phaseId, Sort.by(Sort.Direction.ASC,"id"));

        Assertions.assertNotNull(phaseMaterialList);
        Assertions.assertEquals(1,phaseMaterialList.size());
        Assertions.assertEquals("Cement",phaseMaterialList.getFirst().getName());
    }

    @Test
    public void getMaterialByExposedId(){
        PhaseMaterial phaseMaterial1 = getPhaseMaterial();
        UUID exposedId = phaseMaterial1.getExposedId();

        phaseMaterialRepository.save(phaseMaterial1);

        Optional<PhaseMaterial> optionalPhaseMaterial = phaseMaterialRepository.findByExposedId(exposedId);

        Assertions.assertTrue(optionalPhaseMaterial.isPresent());
        Assertions.assertEquals("Cement",optionalPhaseMaterial.get().getName());
    }

    @Test
    public  void deleteByExposedId(){
        PhaseMaterial phaseMaterial1 = getPhaseMaterial();
        UUID exposedId = phaseMaterial1.getExposedId();

        phaseMaterialRepository.save(phaseMaterial1);

        Optional<PhaseMaterial> optionalPhaseMaterial = phaseMaterialRepository.findByExposedId(exposedId);

        Assertions.assertTrue(optionalPhaseMaterial.isPresent());
        Assertions.assertEquals("Cement",optionalPhaseMaterial.get().getName());

        phaseMaterialRepository.deleteByExposedId(exposedId);

        optionalPhaseMaterial = phaseMaterialRepository.findByExposedId(exposedId);

        Assertions.assertTrue(optionalPhaseMaterial.isEmpty());


    }


}
