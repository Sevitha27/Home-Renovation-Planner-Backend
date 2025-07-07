package com.lowes.repository;

import com.lowes.entity.Skill;
import com.lowes.entity.enums.SkillType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
@Transactional
public class SkillRepositoryTest {
    @Autowired
    private SkillRepository skillRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    @Rollback
    void testFindByNameAndBasePrice() {
        Skill skill = Skill.builder()
                .name(SkillType.PLUMBING)
                .basePrice(100.0)
                .build();
        entityManager.persistAndFlush(skill);

        Optional<Skill> found = skillRepository.findByNameAndBasePrice(SkillType.PLUMBING, 100.0);
        assertTrue(found.isPresent());
        assertEquals(SkillType.PLUMBING, found.get().getName());
        assertEquals(100.0, found.get().getBasePrice());
    }

    @Test
    @Rollback
    void testFindSkillsWithNoVendors() {
        Skill skill = Skill.builder()
                .name(SkillType.ELECTRICAL)
                .basePrice(200.0)
                .build();
        entityManager.persistAndFlush(skill);

        List<Skill> noVendorSkills = skillRepository.findSkillsWithNoVendors();
        assertFalse(noVendorSkills.isEmpty());
        assertTrue(noVendorSkills.stream().anyMatch(s -> s.getName() == SkillType.ELECTRICAL));
    }

    @Test
    @Rollback
    void testFindByName() {
        Skill skill1 = Skill.builder()
                .name(SkillType.CARPENTRY)
                .basePrice(150.0)
                .build();
        Skill skill2 = Skill.builder()
                .name(SkillType.CARPENTRY)
                .basePrice(180.0)
                .build();
        entityManager.persistAndFlush(skill1);
        entityManager.persistAndFlush(skill2);

        List<Skill> carpentrySkills = skillRepository.findByName(SkillType.CARPENTRY);
        assertEquals(2, carpentrySkills.size());
        assertTrue(carpentrySkills.stream().allMatch(s -> s.getName() == SkillType.CARPENTRY));
    }
}
