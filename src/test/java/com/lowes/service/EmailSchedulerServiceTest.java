package com.lowes.service;

import com.lowes.entity.*;
import com.lowes.entity.enums.PhaseStatus;
import com.lowes.repository.PhaseRepository;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;

    public class EmailSchedulerServiceTest {

        @InjectMocks
        private EmailSchedulerService emailSchedulerService;

        @Mock
        private PhaseRepository phaseRepository;

        @Mock
        private JavaMailSender mailSender;

        @Mock
        private MimeMessage mimeMessage;

        @Captor
        private ArgumentCaptor<MimeMessage> mimeMessageCaptor;

        @BeforeEach
        public void setUp() throws Exception {
            MockitoAnnotations.openMocks(this);
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        }

        @Test
        public void testSendPhaseReminders_sendsUpcomingAndOverdueEmails() throws Exception {
            // Arrange
            LocalDate today = LocalDate.now();
            LocalDate in3Days = today.plusDays(3);
            LocalDate yesterday = today.minusDays(1);

            Phase upcomingPhase = buildMockPhase("Painting", in3Days, in3Days.plusDays(2), PhaseStatus.NOTSTARTED);
            Phase overduePhase = buildMockPhase("Plumbing", yesterday.minusDays(2), yesterday, PhaseStatus.INPROGRESS);

            when(phaseRepository.findByStartDateWithDetails(in3Days)).thenReturn(List.of(upcomingPhase));
            when(phaseRepository.findByEndDateBeforeAndPhaseStatusNotWithDetails(today, PhaseStatus.COMPLETED))
                    .thenReturn(List.of(overduePhase));

            emailSchedulerService.sendPhaseReminders();

            verify(mailSender, times(2)).createMimeMessage();
            verify(mailSender, times(2)).send(mimeMessage);
        }

        private Phase buildMockPhase(String name, LocalDate start, LocalDate end, PhaseStatus status) {
            User owner = new User();
            owner.setName("Vinuta");
            owner.setEmail("vinuta@example.com");

            Project project = new Project();
            project.setName("Kitchen Reno");
            project.setOwner(owner);

            Room room = new Room();
            room.setProject(project);

            Phase phase = new Phase();
            phase.setPhaseName(name);
            phase.setStartDate(start);
            phase.setEndDate(end);
            phase.setPhaseStatus(status);
            phase.setRoom(room);

            return phase;
        }
    }


