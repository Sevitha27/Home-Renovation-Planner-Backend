//package com.lowes.repository;
//
//import com.lowes.entity.Phase;
//import com.lowes.entity.Room;
//import com.lowes.entity.Vendor;
//import com.lowes.entity.enums.PhaseStatus;
//import com.lowes.entity.enums.PhaseType;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//
//import java.time.LocalDate;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
//public class PhaseRepositoryTest {
//
//    @Autowired
//    PhaseRepository phaseRepository;
//
//    @Autowired
//    VendorRepository vendorRepository;
//
//    @Autowired
//    RoomRepository roomRepository;
//
//
//    @Test
//    public void whenPhaseSaved_returnNonNullPhase() {
//        Vendor vendor = new Vendor();
//        vendor.setExposedId(UUID.randomUUID());
//        vendor.setCompanyName(
//                "Mock Vendor");
//        vendor = vendorRepository.save(vendor);
//
//        Room room = new Room();
//        room.setExposedId(UUID.randomUUID());
//        room.setName("Mock Room");
//        room = roomRepository.save(room);
//
//        Phase phase = Phase.builder()
//                .vendor(vendor)
//                .room(room)
//                .phaseName("Execution Phase")
//                .description("Execution phase of the project")
//                .startDate(LocalDate.now())
//                .endDate(LocalDate.now().plusDays(5))
//                .phaseType(PhaseType.PAINTING)
//                .phaseStatus(PhaseStatus.INPROGRESS)
//                .build();
//
//        Phase saved = phaseRepository.save(phase);
//
//        assertNotNull(saved.getId());
//        assertEquals("Execution Phase", saved.getPhaseName());
//        assertEquals(vendor.getExposedId(), saved.getVendor().getExposedId());
//        assertEquals(room.getId(), saved.getRoom().getId());
//    }
//
//
//
//
//
//}
