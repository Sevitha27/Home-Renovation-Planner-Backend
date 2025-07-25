//package com.lowes.repository;
//
//import com.lowes.entity.*;
//import com.lowes.entity.enums.PhaseStatus;
//import com.lowes.entity.enums.PhaseType;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//class PhaseRepositoryTest {
//
//    @Autowired
//    private PhaseRepository phaseRepository;
//
//    @Autowired
//    private RoomRepository roomRepository;
//
//    @Autowired
//    private VendorRepository vendorRepository;
//
//    private Room room;
//    private Vendor vendor;
//    private Phase phase1, phase2;
//
//    @BeforeEach
//    void setUp() {
//        // Persist Room
//        room = new Room();
//        room.setName("Test Room");
//        room.setExposedId(UUID.randomUUID());
//        room = roomRepository.save(room); // <-- Save to DB and reassign with managed entity
//
//        // Persist Vendor
//        vendor = new Vendor();
//        vendor.setCompanyName("Test Vendor");
//        vendor = vendorRepository.save(vendor);
//
//        // Now associate managed Room and Vendor with Phase
//        phase1 = new Phase();
//        phase1.setRoom(room);
//        phase1.setVendor(vendor);
//        phase1.setStartDate(LocalDate.now());
//        phase1.setEndDate(LocalDate.now().plusDays(5));
//        phase1.setPhaseStatus(PhaseStatus.INPROGRESS);
//        phase1.setPhaseType(PhaseType.PLUMBING);
//        phase1.setPhaseName("Phase 1");
//        phaseRepository.save(phase1);
//
//        phase2 = new Phase();
//        phase2.setRoom(room);
//        phase2.setVendor(vendor);
//        phase2.setStartDate(LocalDate.now());
//        phase2.setEndDate(LocalDate.now().minusDays(1));
//        phase2.setPhaseStatus(PhaseStatus.COMPLETED);
//        phase2.setPhaseType(PhaseType.PLUMBING);
//        phase2.setPhaseName("Phase 2");
//        phaseRepository.save(phase2);
//    }
//
//
//    @Test
//    void testFindByEndDateBeforeAndPhaseStatusNot() {
//        List<Phase> results = phaseRepository.findByEndDateBeforeAndPhaseStatusNot(LocalDate.now(), PhaseStatus.COMPLETED);
//        assertThat(results).isEmpty();
//    }
//
//    @Test
//    void testFindByStartDate() {
//        List<Phase> results = phaseRepository.findByStartDate(LocalDate.now());
//        assertThat(results).hasSize(2);
//    }
//
//    @Test
//    void testExistsByRoomIdAndPhaseType() {
//        boolean exists = phaseRepository.existsByRoomIdAndPhaseType(room.getId(), PhaseType.PLUMBING);
//        assertThat(exists).isTrue();
//    }
//
//    @Test
//    void testFindAllByRoom_Id() {
//        List<Phase> results = phaseRepository.findAllByRoom_Id(room.getId());
//        assertThat(results).hasSize(2);
//    }
//
//    @Test
//    void testFindByVendor() {
//        List<Phase> results = phaseRepository.findByVendor(vendor);
//        assertThat(results).hasSize(2);
//    }
//
//    @Test
//    void testFindAllByRoom_ExposedId() {
//        List<Phase> results = phaseRepository.findAllByRoom_ExposedId(room.getExposedId());
//        assertThat(results).hasSize(2);
//    }
//}
//
