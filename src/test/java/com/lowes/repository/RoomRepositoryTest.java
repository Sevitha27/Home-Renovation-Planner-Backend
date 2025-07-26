package com.lowes.repository;

import com.lowes.entity.Project;
import com.lowes.entity.Room;
import com.lowes.entity.User;
import com.lowes.entity.enums.RenovationType;
import com.lowes.entity.enums.Role;
import com.lowes.entity.enums.ServiceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class RoomRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;
    private Project project;
    private Room room;
    private UUID roomExposedId;
    private UUID projectExposedId;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setRole(Role.CUSTOMER);
        user.setExposedId(UUID.randomUUID());
        entityManager.persistAndFlush(user);

        projectExposedId = UUID.randomUUID();

        project = Project.builder()
                .name("Test Project")
                .exposedId(projectExposedId)
                .serviceType(ServiceType.ROOM_WISE)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(2))
                .estimatedBudget(10000)
                .owner(user)
                .build();
        entityManager.persistAndFlush(project);

        roomExposedId = UUID.randomUUID();

        room = Room.builder()
                .name("Living Room")
                .exposedId(roomExposedId)
                .renovationType(RenovationType.LIVING_ROOM_REMODEL)

                .project(project)
                .build();
        entityManager.persistAndFlush(room);
    }

    @Test
    @Rollback
    void whenFindByExposedId_thenReturnRoom() {
        Optional<Room> found = roomRepository.findByExposedId(roomExposedId);
        assertTrue(found.isPresent());
        assertEquals("Living Room", found.get().getName());
    }

    @Test
    @Rollback
    void whenFindByInvalidExposedId_thenReturnEmpty() {
        Optional<Room> found = roomRepository.findByExposedId(UUID.randomUUID());
        assertTrue(found.isEmpty());
    }

    @Test
    @Rollback
    void whenFindByProjectExposedId_thenReturnRooms() {
        List<Room> rooms = roomRepository.findByProjectExposedId(projectExposedId);
        assertEquals(1, rooms.size());
        assertEquals("Living Room", rooms.get(0).getName());
    }

    @Test
    @Rollback
    void whenFindByInvalidProjectExposedId_thenReturnEmptyList() {
        List<Room> rooms = roomRepository.findByProjectExposedId(UUID.randomUUID());
        assertTrue(rooms.isEmpty());
    }

    @Test
    @Rollback
    void whenExistsByExposedIdAndOwnerExposedId_thenReturnTrue() {
        boolean exists = roomRepository.existsByExposedIdAndProjectOwnerExposedId(
                roomExposedId,
                user.getExposedId()
        );
        assertTrue(exists);
    }

    @Test
    @Rollback
    void whenNotExistsByExposedIdAndOwnerExposedId_thenReturnFalse() {
        boolean exists = roomRepository.existsByExposedIdAndProjectOwnerExposedId(
                UUID.randomUUID(),
                user.getExposedId()
        );
        assertFalse(exists);
    }

    @Test
    @Rollback
    void whenFindByExposedIdAndOwnerId_thenReturnRoom() {
        Optional<Room> found = roomRepository.findByExposedIdAndOwnerId(
                roomExposedId,
                user.getId()
        );
        assertTrue(found.isPresent());
        assertEquals("Living Room", found.get().getName());
    }

    @Test
    @Rollback
    void whenFindByExposedIdAndInvalidOwnerId_thenReturnEmpty() {
        Optional<Room> found = roomRepository.findByExposedIdAndOwnerId(
                roomExposedId,
                999L
        );
        assertTrue(found.isEmpty());
    }
}
