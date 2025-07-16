package com.lowes.repository;

import com.lowes.entity.User;
import com.lowes.entity.enums.Role;
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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
@Transactional
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    @Rollback
    void testFindByEmail() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setRole(Role.CUSTOMER);
        entityManager.persistAndFlush(user);

        Optional<User> found = userRepository.findByEmail("test@example.com");
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    @Rollback
    void testFindByExposedId() {
        User user = new User();
        UUID exposedId = UUID.randomUUID();
        user.setExposedId(exposedId);
        user.setEmail("exposed@example.com");
        user.setRole(Role.CUSTOMER);
        entityManager.persistAndFlush(user);

        User found = userRepository.findByExposedId(exposedId);
        assertNotNull(found);
        assertEquals(exposedId, found.getExposedId());
    }

    @Test
    @Rollback
    void testFindByRole() {
        User user1 = new User();
        user1.setEmail("customer1@example.com");
        user1.setRole(Role.CUSTOMER);
        entityManager.persistAndFlush(user1);

        User user2 = new User();
        user2.setEmail("customer2@example.com");
        user2.setRole(Role.CUSTOMER);
        entityManager.persistAndFlush(user2);

        User user3 = new User();
        user3.setEmail("vendor@example.com");
        user3.setRole(Role.VENDOR);
        entityManager.persistAndFlush(user3);

        Pageable pageable = PageRequest.of(0, 10);
        Page<User> customers = userRepository.findByRole(Role.CUSTOMER, pageable);
        assertEquals(2, customers.getTotalElements());
        assertTrue(customers.getContent().stream().allMatch(u -> u.getRole() == Role.CUSTOMER));
    }
}
