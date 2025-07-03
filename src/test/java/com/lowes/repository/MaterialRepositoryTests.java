package com.lowes.repository;

import com.lowes.entity.Material;
import com.lowes.entity.PhaseMaterial;
import com.lowes.entity.enums.PhaseType;
import com.lowes.entity.enums.Unit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class MaterialRepositoryTests {

    @Autowired
    MaterialRepository materialRepository;

    private Material getMaterial(){
        Material material = Material.builder()
                .name("Cement")
                .exposedId(UUID.randomUUID())
                .unit(Unit.KG)
                .phaseType(PhaseType.CIVIL)
                .pricePerQuantity(100)
                .build();


        return material;
    }

    @Test
    public void whenMaterialIsSavedThenReturnNonNullMaterial(){

        Material material = getMaterial();

        Material savedMaterial = materialRepository.save(material);

        Assertions.assertNotNull(savedMaterial);
        Assertions.assertNotEquals(0,savedMaterial.getId());
        Assertions.assertNotNull(savedMaterial.getExposedId());
        Assertions.assertEquals("Cement",savedMaterial.getName());
//        Assertions.assertTrue(savedMaterial.getPhaseMaterialList() instanceof List<PhaseMaterial>);
//        Assertions.assertEquals(0,savedMaterial.getPhaseMaterialList().size());
        Assertions.assertEquals(false,material.isDeleted());
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

    @Test
    public void getMaterialsByPhaseTypeAndDeleted(){
        Material material1 = getMaterial();

        Material material2 = getMaterial();
        material2.setName("Wires");
        material2.setPhaseType(PhaseType.ELECTRICAL);

        Material material3 = getMaterial();
        material3.setName("Steel");
        material3.setDeleted(true);

        Material material4 = getMaterial();
        material4.setName("Wood");
        material4.setPhaseType(PhaseType.CARPENTRY);
        material4.setDeleted(true);

        materialRepository.saveAll(List.of(material1,material2,material3,material4));

        List<Material> materialList = materialRepository.findByPhaseTypeAndDeleted(PhaseType.CIVIL,false, Sort.by(Sort.Direction.ASC,"id"));

        Assertions.assertEquals(1,materialList.size());
        Assertions.assertEquals("Cement",materialList.getFirst().getName());
        Assertions.assertNotNull(materialList.getFirst().getExposedId());
    }

    @Test
    public void getMaterialsByPhaseType(){
        Material material1 = getMaterial();

        Material material2 = getMaterial();
        material2.setName("Wires");
        material2.setPhaseType(PhaseType.ELECTRICAL);

        materialRepository.saveAll(List.of(material1,material2));

        List<Material> materialList = materialRepository.findByPhaseType(PhaseType.CIVIL, Sort.by(Sort.Direction.ASC,"deleted","id"));

        Assertions.assertEquals(1,materialList.size());
        Assertions.assertEquals("Cement",materialList.getFirst().getName());
        Assertions.assertNotNull(materialList.getFirst().getExposedId());
    }

    @Test
    public void getMaterialsByDeleted(){
        Material material1 = getMaterial();

        Material material2 = getMaterial();
        material2.setName("Steel");
        material2.setDeleted(true);

        materialRepository.saveAll(List.of(material1,material2));

        List<Material> materialList = materialRepository.findByDeleted(false, Sort.by(Sort.Direction.ASC,"phaseType","id"));

        Assertions.assertEquals(1,materialList.size());
        Assertions.assertEquals("Cement",materialList.getFirst().getName());
        Assertions.assertNotNull(materialList.getFirst().getExposedId());
    }

    @Test
    public void getMaterials(){
        Material material1 = getMaterial();

        Material material2 = getMaterial();
        material2.setName("Wires");
        material2.setPhaseType(PhaseType.ELECTRICAL);

        Material material3 = getMaterial();
        material3.setName("Steel");
        material3.setDeleted(true);

        Material material4 = getMaterial();
        material4.setName("Wood");
        material4.setPhaseType(PhaseType.CARPENTRY);
        material4.setDeleted(true);

        materialRepository.saveAll(List.of(material1,material2,material3,material4));

        List<Material> materialList = materialRepository.findAll(Sort.by(Sort.Direction.ASC,"deleted","phaseType","id"));

        Assertions.assertEquals(4,materialList.size());
        Assertions.assertNotNull(materialList.getFirst().getExposedId());
    }

    @Test
    public void getMaterialByExposedId(){
        Material material1 = getMaterial();
        UUID exposedId = material1.getExposedId();

        Material material2 = getMaterial();
        material2.setName("Wires");
        material2.setPhaseType(PhaseType.ELECTRICAL);

        materialRepository.saveAll(List.of(material1,material2));

        Optional<Material> optionalMaterial = materialRepository.findByExposedId(exposedId);

        Assertions.assertTrue(optionalMaterial.isPresent());
        Assertions.assertEquals("Cement",optionalMaterial.get().getName());
    }












}
