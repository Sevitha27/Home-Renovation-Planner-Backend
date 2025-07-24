package com.lowes.repository;

import com.lowes.entity.*;
import com.lowes.entity.enums.RenovationType;
import com.lowes.entity.enums.ServiceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class RoomRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    private UUID roomExposedId;
    private UUID userExposedId;
    private UUID projectExposedId;
    private Long userId;

    @BeforeEach
    public void setUp() {
        // Create user
        User owner = new User();
        owner.setName("Muskan");
        owner.setEmail("muskan@example.com");
        owner.setExposedId(UUID.randomUUID());
        userExposedId = owner.getExposedId();
        owner = userRepository.save(owner); // Save and reassign
        userId = owner.getId();

        // Create project
        Project project = new Project();
        project.setName("Test Project");
        project.setExposedId(UUID.randomUUID());
        project.setOwner(owner);
        project = projectRepository.save(project); // Save and reassign
        projectExposedId = project.getExposedId();

        // Create room
        Room room = new Room();
        room.setName("Living Room");
        room.setExposedId(UUID.randomUUID());
        room.setRenovationType(RenovationType.BEDROOM_RENOVATION);
        room.setProject(project);
        room = roomRepository.save(room); // Save and reassign
        roomExposedId = room.getExposedId();
    }

    @Test
    void testExistsByExposedIdAndProjectOwnerExposedId() {
        boolean exists = roomRepository.existsByExposedIdAndProjectOwnerExposedId(roomExposedId, userExposedId);
        assertThat(exists).isTrue();
    }

    @Test
    void testFindByProjectExposedId() {
        List<Room> rooms = roomRepository.findByProjectExposedId(projectExposedId);
        assertThat(rooms).isNotEmpty();
        assertThat(rooms.get(0).getProject().getExposedId()).isEqualTo(projectExposedId);
    }

    @Test
    void testFindByExposedId() {
        Optional<Room> room = roomRepository.findByExposedId(roomExposedId);
        assertThat(room).isPresent();
        assertThat(room.get().getExposedId()).isEqualTo(roomExposedId);
    }

    @Test
    void testFindByExposedIdAndProject_Owner_Id() {

        Optional<Room> room = roomRepository.findByExposedId(roomExposedId);
        assertThat(room).isPresent();
        assertThat(room.get().getProject().getOwner().getId()).isEqualTo(userId);
    }
}