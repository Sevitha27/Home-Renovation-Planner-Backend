package com.lowes.repository;

import com.lowes.entity.Material;
import com.lowes.entity.Phase;
import com.lowes.entity.PhaseMaterial;
import com.lowes.entity.enums.PhaseStatus;
import com.lowes.entity.enums.PhaseType;
import com.lowes.entity.enums.Unit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.ArrayList;
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

        Material savedMaterial = materialRepository.save(material);

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
                .project(null)
                .vendor(null)
                .build();

        Phase savedPhase = phaseRepository.save(phase);

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
//        material.getPhaseMaterialList().add(phaseMaterial);
//        phase.getPhaseMaterialList().add(phaseMaterial);




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
//        Assertions.assertEquals(1,savedPhaseMaterial.getMaterial().getPhaseMaterialList().size());
//        Assertions.assertEquals(1,savedPhaseMaterial.getPhase().getPhaseMaterialList().size());


    }

    @Test
    public void whenExposedIdIsNullThenThrowException(){
        Material material = getMaterial();
        material.setExposedId(null);

        Assertions.assertThrows(DataIntegrityViolationException.class, ()->{materialRepository.save(material);});
    }

    @Test
    public void whenNameIsNullThenThrowException(){
        Material material = getMaterial();
        material.setName(null);

        Assertions.assertThrows(DataIntegrityViolationException.class, ()->{materialRepository.save(material);});
    }

    @Test
    public void whenUnitIsNullThenThrowException(){
        Material material = getMaterial();
        material.setUnit(null);

        Assertions.assertThrows(DataIntegrityViolationException.class, ()->{materialRepository.save(material);});
    }

    @Test
    public void whenPhaseTypeIsNullThenThrowException(){
        Material material = getMaterial();
        material.setPhaseType(null);

        Assertions.assertThrows(DataIntegrityViolationException.class, ()->{materialRepository.save(material);});
    }

    @Test
    public void whenNameIdIsNotUniqueThenThrowException(){
        Material material1 = getMaterial();
        materialRepository.save(material1);
        Material material2 = getMaterial();

        Assertions.assertThrows(DataIntegrityViolationException.class, ()->{materialRepository.save(material2);});
    }
}
