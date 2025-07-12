package com.lowes.repository;

import com.lowes.entity.Skill;
import com.lowes.entity.User;
import com.lowes.entity.Vendor;
import com.lowes.entity.enums.Role;
import com.lowes.entity.enums.SkillType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
@Transactional
public class VendorRepositoryTest {
    @Autowired
    private VendorRepository vendorRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    @Rollback
    void testFindByExposedId() {
        Vendor vendor = new Vendor();
        UUID exposedId = UUID.randomUUID();
        vendor.setExposedId(exposedId);
        entityManager.persistAndFlush(vendor);

        Vendor found = vendorRepository.findByExposedId(exposedId);
        assertNotNull(found);
        assertEquals(exposedId, found.getExposedId());
    }

    @Test
    @Rollback
    void testFindByApproved() {
        Vendor vendor1 = new Vendor();
        vendor1.setApproved(true);
        entityManager.persistAndFlush(vendor1);

        Vendor vendor2 = new Vendor();
        vendor2.setApproved(false);
        entityManager.persistAndFlush(vendor2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Vendor> approvedVendors = vendorRepository.findByApproved(true, pageable);
        assertEquals(1, approvedVendors.getTotalElements());
        assertTrue(approvedVendors.getContent().stream().allMatch(Vendor::getApproved));
    }

    @Test
    @Rollback
    void testFindByApprovedIsNull() {
        Vendor vendor = new Vendor();
        vendor.setApproved(null);
        entityManager.persistAndFlush(vendor);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Vendor> pendingVendors = vendorRepository.findByApprovedIsNull(pageable);
        assertEquals(1, pendingVendors.getTotalElements());
        assertNull(pendingVendors.getContent().get(0).getApproved());
    }

    @Test
    @Rollback
    void testFindByUser() {
        User user = new User();
        user.setEmail("vendoruser@example.com");
        user.setRole(Role.VENDOR);
        entityManager.persistAndFlush(user);

        Vendor vendor = new Vendor();
        vendor.setUser(user);
        entityManager.persistAndFlush(vendor);

        Vendor found = vendorRepository.findByUser(user);
        assertNotNull(found);
        assertEquals(user.getEmail(), found.getUser().getEmail());
    }

    @Test
    @Rollback
    void testCountBySkillsContaining() {
        Skill skill = Skill.builder()
                .name(SkillType.PLUMBING)
                .basePrice(100.0)
                .build();
        entityManager.persistAndFlush(skill);

        Vendor vendor = new Vendor();
        vendor.setSkills(Collections.singletonList(skill));
        entityManager.persistAndFlush(vendor);

        long count = vendorRepository.countBySkillsContaining(skill);
        assertEquals(1, count);
    }
}
